package br.ufrn.core

/**
 * Counts the amount of logic lines of code excluding non-statement lines
 * */
object LLOC {

    fun analyze(lines: List<String>): Int = Counter(lines).run()

    private class Counter(private val lines: List<String>) {

        private var counter = 0
        private var openedBrackets = 0
        private var closedBrackets = 0

        internal fun run(): Int {
            for (line in lines) {

                val trimmed = line.trim()

                if (trimmed.isEmpty()) continue
                if (isEscaped(trimmed, arrayOf("//", "/*", "*/", "*"))) continue
                if (isEscaped(trimmed, arrayOf("import", "package"))) continue

                countStatementsAndDeclarations(trimmed)
            }
            return counter + if (openedBrackets - closedBrackets == 0) openedBrackets else -1
        }

        private fun countStatementsAndDeclarations(trimmed: String) {
            if (trimmed.contains(";")) {
                counter++
            }

            if (trimmed.contains("{")) {
                openedBrackets += frequency(trimmed, "{")
            } else if (trimmed.length != 1) {
                counter++
            }

            if (trimmed.contains("}")) {
                closedBrackets += frequency(trimmed, "}")
            }
        }

        private fun isEscaped(trimmed: String, rules: Array<String>): Boolean {
            return rules.any { trimmed.startsWith(it) }
        }

        private fun frequency(source: String, part: String): Int {

            if (source.isEmpty() || part.isEmpty()) {
                return 0
            }

            var count = 0
            var pos = source.indexOf(part, 0)
            while (pos != -1) {
                pos += part.length
                count++
                pos = source.indexOf(part, pos)
            }

            return count
        }
    }
}