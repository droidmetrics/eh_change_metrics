package br.ufrn.model

data class GitCommit(val author: Author,
                     val committer : Author,
                     val message: String,
                     val tree: Tree,
                     val url: String,
                     val comment_count: Int,
                     val verification: Verification)