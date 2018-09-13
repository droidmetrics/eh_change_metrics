package br.ufrn.model

data class GitComparison(val url: String,
                         val html_url: String,
                         val permalink_url: String,
                         val diff_url: String,
                         val patch_url: String,
                         val base_commit: Commit,
                         val merge_base_commit: Commit,
                         val status: String,
                         val ahead_by: Int,
                         val behind_by: Int,
                         val total_commits: Int,
                         val commits: List<Commit>,
                         val files: List<File>)