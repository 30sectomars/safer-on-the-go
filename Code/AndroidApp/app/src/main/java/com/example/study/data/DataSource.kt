package com.example.study.data

import com.example.study.model.TableContent

class DataSource {

    fun get2DData(numberOfRows: Int = 15, numberOfColumns: Int = 15): List<List<TableContent>> {
        val list: MutableList<MutableList<TableContent>> = mutableListOf()

        val innerList: MutableList<TableContent> = mutableListOf()

        (1..numberOfColumns).forEach {column ->
            innerList.clear()
            (1..numberOfRows).forEach {row ->
                innerList.add(TableContent(row = row, column = column))
            }
            list.add(innerList.toMutableList())
        }
        return list.toList()
    }

    fun loadColumnData(numberOfColumns: Int = 15): List<TableContent> {
        val list: MutableList<TableContent> = mutableListOf()

        (1..numberOfColumns).forEach {
            list.add(TableContent(1, it))
        }

        return list.toList()
    }

    fun loadRowData(numberOfRows: Int = 15): List<TableContent> {
        val list: MutableList<TableContent> = mutableListOf()

        (1..numberOfRows).forEach {
            list.add(TableContent(it, 1))
        }

        return list.toList()
    }

    fun getParticipantIDs(): List<String> {
        return (0..23).map { it.toString() }
    }

    fun getSelectionMethods(): List<String> {
        return listOf("Direct Touch", "Buttons", "Indirect Touch", "Analogue Stick")
    }

    fun getRemoteMountingSides(): List<String> {
        return listOf("Left", "Right")
    }
}