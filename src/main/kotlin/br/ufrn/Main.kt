/*
package br.ufrn

import br.ufrn.model.GitComparison
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.RepositoryBuilder
import java.io.File

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request

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
//                Analyser().analyzePatch(it.patch)
                println( it.filename )
            }
}

internal fun configRepository(user: String, v: String, repo: String): Repository {
    val folder = File("/Users/alice/Documents/TCC/change_metrics/$user/$v/$repo/")//File("C:\\change_metrics\\$user\\$v\\$repo")
    val repository: Repository
    val builder = RepositoryBuilder()

//    if (folder.exists()) {
//        repository = builder
//                .setGitDir(File(folder, ".git"))
//                .readEnvironment()
//                .findGitDir()
//                .build()
//
//    } else {
        println("Cloning https://github.com/$user/$repo/.git ...")
        val git = Git.cloneRepository()
                .setDirectory(folder)
                .setURI("https://github.com/$user/$repo.git")
                .call()
        repository = git.repository
//    }
    Git(repository).use { git ->
        val checkout = git.checkout().setName(repository.findRef(v).objectId.name)
        checkout.call()
    }
    return repository
}

fun String.hasEhmKeywords() = (""".*\b(throw|try|catch|finally|Throws)\b.*""".toRegex(RegexOption.MULTILINE).containsMatchIn(this))*/
