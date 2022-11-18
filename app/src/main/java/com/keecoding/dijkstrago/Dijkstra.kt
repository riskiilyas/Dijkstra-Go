package com.keecoding.dijkstrago

import kotlinx.coroutines.*

class Dijkstra(
    private val callback: (MutableList<MutableList<Int>>) -> Unit,
    private val onFinish: (MutableList<MutableList<Int>>, Int) -> Unit,
    private val onCantFind: () -> Unit
) {
    private var mMatrix: MutableList<MutableList<Int>> = mutableListOf()
    private var isFinding = false
    private var watcher = 0
    private var startX = -1
    private var startY = -1

    fun setMatrix(mMatrix: MutableList<MutableList<Int>>) {
        this.mMatrix = mMatrix
    }

    private fun observeMoveChanges() {
        val ctr = watcher
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            if (ctr == watcher && isFinding) {
                onCantFind.invoke()
                return@launch
            } else if (!isFinding) {
                return@launch
            }
            observeMoveChanges()
        }
    }

    fun startFinding(
        paths: MutableList<Pair<Int, Int>> = mutableListOf(),
        x: Int = -1,
        y: Int = -1
    ) {
        val newPaths = mutableListOf<Pair<Int, Int>>()
        paths.forEach { newPaths.add(it) }
        if (paths.isEmpty()) {
            isFinding = true
            var x2 = -1
            var y2 = -1
            mMatrix.forEachIndexed { index, mutableList ->
                mutableList.forEachIndexed { index2, i ->
                    if (i == 1) {
                        x2 = index2
                        y2 = index
                        startX = index2
                        startY = index
                    }
                }
            }
            observeMoveChanges()
            newPaths.add(Pair(x2, y2))
            startFinding(newPaths, x2, y2)
        } else {
            if (!isFinding) return
            if (mMatrix[y][x] == 2) {
                isFinding = false
                paths.forEach {
                    mMatrix[it.second][it.first] = 4
                }
                mMatrix[y][x] = 2
                onFinish.invoke(mMatrix, paths.size)
                return
            }
            if (mMatrix[y][x] != 1) {
                mMatrix[y][x] = 3
                newPaths.add(Pair(x, y))
            }
            callback.invoke(mMatrix)

            GlobalScope.launch(Dispatchers.Default) {
                delay(150)


                launch {
                    delay(60)

                    if (y > 0) {
                        if (mMatrix[y - 1][x] == 0 || mMatrix[y - 1][x] == 2) {
                            watcher++
                            startFinding(newPaths, x, y - 1)
                        }
                    }
                }

                delay(60)
                launch {

                    if (y < Constants.yBlocks-1) {
                        if (mMatrix[y + 1][x] == 0 || mMatrix[y + 1][x] == 2) {
                            watcher++
                            startFinding(newPaths, x, y + 1)
                        }
                    }
                }

                delay(60)

                launch {

                    if (x > 0) {
                        if (mMatrix[y][x - 1] == 0 || mMatrix[y][x - 1] == 2) {
                            watcher++
                            startFinding(newPaths, x - 1, y)
                        }
                    }
                }

                delay(60)

                launch {

                    if (x < Constants.xBlocks-1) {
                        if (mMatrix[y][x + 1] == 0 || mMatrix[y][x + 1] == 2) {
                            watcher++
                            startFinding(newPaths, x + 1, y)
                        }
                    }
                }

            }
        }
    }
}