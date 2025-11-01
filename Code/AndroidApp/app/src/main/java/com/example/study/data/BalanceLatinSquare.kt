package com.example.study.data

class BalanceLatinSquare {
    private val inputMethods = listOf("Direct Touch", "Buttons", "Analogue Stick", "Indirect Touch")
    private val selectionMethods = listOf("Vertical", "Horizontal", "2D")

    val balanceLatinSquare: MutableMap<String, MutableMap<String, List<String>>> = mutableMapOf()
    var walkthrough = mutableListOf<String>()

    init {
        for (  i in 0 .. 23 ) {
            val inputs = bls(inputMethods, i)
            for ( j in inputs.indices ) {
                balanceLatinSquare.getOrPut(i.toString()) { mutableMapOf() }[inputs[j]] = bls(selectionMethods, i * 4 + j)
            }
        }
    }

    private fun bls(array: List<String>, participantId: Int): List<String> {
        /**
         * Generates a balanced Latin square based on the given array and participant ID.
         *
         * @param array The list of conditions.
         * @param participantId The participant ID.
         * @return The balanced Latin square as a list.
         */

        val result = mutableListOf<String>()
        var j = 0
        var h = 0

        // Based on "Bradley, J. V. Complete counterbalancing of immediate sequential effects
        // in a Latin square design. J. Amer. Statist. Ass.,.1958, 53, 525-528. "
        for (i in array.indices) {
            val valIndex = if (i < 2 || i % 2 != 0) {
                j.also { j++ }
            } else {
                (array.size - h - 1).also { h++ }
            }

            val idx = (valIndex + participantId) % array.size
            result.add(array[idx])
        }

        if (array.size % 2 != 0 && participantId % 2 != 0) {
            result.reverse()
        }

        return result
    }
}
