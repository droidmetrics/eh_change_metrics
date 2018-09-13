package br.ufrn


import br.ufrn.model.GitComparison
import com.google.gson.GsonBuilder
import java.io.File
import java.util.*
import com.intellij.psi.PsiElement
import kastree.ast.Node
import kastree.ast.psi.Converter
import kastree.ast.psi.Parser
import okhttp3.OkHttpClient
import okhttp3.Request
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.RepositoryBuilder
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation

fun main(args: Array<String>) {

    if (args.isEmpty()) {
        throw Exception("Empty args!")
    }

    // RULES
    // The analysis must start with the oldest version to be compared, that's how the first set of Exceptions is populated

    // 1st step:
    // Visit all nodes of the oldest version and map exceptions already declared
    // MAP < Class , Exception > ?

    val user = args[0]
    val repo = args[1]
    val vnm1 = args[2]
    val vn = args[3]

    val olderVersion: Repository = configRepository(user, vnm1, repo)
    val newerVersion: Repository = configRepository(user, vn, repo)


    println("Retrieving changes between $vnm1 and $vn from https://api.github.com/repos/$user/$repo/compare/$vnm1...$vn")
    val request = try { Request.Builder().url("https://api.github.com/repos/$user/$repo/compare/$vnm1...$vn").build() } catch (_ : Exception) {  throw Exception("Invalid args!") } as Request
    val response = OkHttpClient().newCall(request).execute()
    val comparison = GsonBuilder().create().fromJson(response?.body()?.string(), GitComparison::class.java)
    println("\nFILES CHANGED ${comparison.files.size} ")
    comparison.files.forEach{ println( it.filename ) }

    println("\n\nKT FILES CHANGED ${comparison.files.filter{ it.filename.endsWith(".kt")}.size}")
    comparison.files.filter{ it.filename.endsWith(".kt")}
            .forEach{ println( it.filename ) }

    println("\n\nKT FILES WITH EHM CHANGED ${comparison.files.filter{ it.filename.endsWith(".kt")}.filter{ it.patch.hasEhmKeywords() }.size}")
    comparison.files.filter{ it.filename.endsWith(".kt")}
            .filter{ it.patch.hasEhmKeywords() }
            .forEach{
                println( it.filename )
                analyzeChanges(it, olderVersion, newerVersion)
            }
}

internal fun configRepository(user: String, v: String, repo: String): Repository {
    val folderVersion = File("/Users/alice/Documents/TCC/change_metrics/$user/$v/$repo")//File("C:\\change_metrics\\$user\\$v\\$repo")
    val repository: Repository
    val builder = RepositoryBuilder()

    if (folderVersion.exists()) {
        repository = builder
                .setGitDir(File(folderVersion, ".git"))
                .readEnvironment()
                .findGitDir()
                .build()

    } else {
        println("Cloning https://github.com/$user/$repo/.git ...")
        val git = Git.cloneRepository()
                .setDirectory(folderVersion)
                .setURI("https://github.com/$user/$repo.git")
                .call()
        repository = git.repository
    }
    Git(repository).use { git ->
        val checkout = git.checkout().setName(repository.findRef(v).objectId.name)
        checkout.call()
    }
    return repository
}

fun String.hasEhmKeywords() = (""".*\b(throw|try|catch|finally|Throws)\b.*""".toRegex(RegexOption.MULTILINE).containsMatchIn(this))

fun analyzeChanges(file : br.ufrn.model.File,  olderVersion: Repository,  newerVersion: Repository) {

    val oldFile = File(olderVersion.directory.canonicalPath.replace(".git","") + file.filename)//.replace("/","\\"))
    val newFile = File(newerVersion.directory.canonicalPath.replace(".git","") + file.filename)//.replace("/","\\"))

    val changes = file.patch.split("@@")
    val changesMap = IdentityHashMap<String,String>()
    changes.forEachIndexed{i, codeSnippet -> if (i>0 && i%2==1 && i<changes.size-1) changesMap[codeSnippet]=changes[i+1]}
    val lineList = mutableListOf<String>()
    changesMap.filter{ it.value.hasEhmKeywords() }
            .forEach {
                t, u -> u.split("\n").forEach { if (it.startsWith("+") || it.startsWith("-")) lineList.add(it) }
                lineList.forEachIndexed{i, line -> println("line $i ==> content $line")}
                lineList.forEach {
                    if (!it.isBlank() && it.startsWith("-") && u.hasEhmKeywords())
                        oldFile.checkEHMOccurrences(t, it)
                    else if (!it.isBlank() && it.startsWith("+") && u.hasEhmKeywords())
                        newFile.checkEHMOccurrences(t, it)
        }
    }

}


fun File.checkEHMOccurrences(region : String, codeLine : String) {

    println("region $region ==> codeline $codeLine")

    try {
        val elemMap = IdentityHashMap<Node, PsiElement>()
        val origExtrasConv = object : Converter.WithExtras() {
            override fun onNode(node: Node, elem: PsiElement) {
                elemMap[node] = elem
                super.onNode(node, elem)
            }
        }

        val sourceCode = this.bufferedReader().use { it.readText() }
        Parser(origExtrasConv).parseFile(sourceCode)

        elemMap.forEach {
            if (it.key is Node.Modifier.AnnotationSet.Annotation && (it.key as Node.Modifier.AnnotationSet.Annotation).names.contains("Throws")) {
                println("Throws")
                println(it.value.getTextWithLocation())
            }
            if (it.key is Node.Expr.Try) {
                println("Try")
                println(it.value.getTextWithLocation())
            }
            if (it.key is Node.Expr.Try.Catch) {
                println("Catch")
                println(it.value.getTextWithLocation())
            }
            if (it.key is Node.Expr.Throw) {
                println("Throw")
                println(it.value.getTextWithLocation())
            }
        }
    } catch (e: Converter.Unsupported) {
        e.printStackTrace()
    } catch (e: Parser.ParseError) {
        e.printStackTrace()
    }
}