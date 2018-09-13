package br.ufrn.model

data class Commit(val sha: String,
                  val node_id: String,
                  val commit: GitCommit,
                  val url: String,
                  val html_url: String,
                  val comments_url: String,
                  val author: User,
                  val committer: User,
                  val parents: List<Node>)