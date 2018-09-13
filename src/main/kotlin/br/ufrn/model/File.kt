package br.ufrn.model

data class File(val sha: String,
                val filename: String,
                val status: String,
                val additions: Int,
                val deletions: Int,
                val changes: Int,
                val blob_url: String,
                val raw_url: String,
                val contents_url: String,
                val patch: String)