package com.keecoding.dijkstrago

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.keecoding.dijkstrago.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dijkstra: Dijkstra
    private var isSetStart = false
    private var isSetEnd = false
    private var isPlaying = false
    private var isFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dijkstra = Dijkstra(
            {
                binding.panel.callback(it)
            },
            { list, moves ->
                binding.panel.finishMove(list)
                runOnUiThread {
                    Toast.makeText(this, "$moves Moves", Toast.LENGTH_SHORT).show()
                }
                isFinished = true
                isPlaying = false
            }, {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Can't Find the Path!", Toast.LENGTH_SHORT)
                        .show()
                }
                binding.panel.cantFind()
                isFinished = true
                isPlaying = false
            }
        )

        binding.btnSetStart.setOnClickListener {
            if (!isPlaying && !isFinished) {
                isSetStart = true
                isSetEnd = false
                binding.panel.setMode(PanelView.MODE_START)
            }
        }

        binding.btnSetEnd.setOnClickListener {
            if (!isPlaying && !isFinished) {
                isSetEnd = true
                isSetStart = false
                binding.panel.setMode(PanelView.MODE_END)
            }
        }

        binding.btnPlay.setOnClickListener {
            if (!isPlaying && !isFinished) {
                if (!binding.panel.isReady()) {
                    Toast.makeText(this, "Please, Set Start & End First!", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                isPlaying = true
                isSetStart = false
                isSetEnd = false
                val matrixList = binding.panel.provideMatrix()
                binding.panel.play()
                dijkstra.setMatrix(matrixList)
                dijkstra.startFinding()
            } else {
                Toast.makeText(this, "Wait to Finish or Clear First!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnClear.setOnClickListener {
            if (!isPlaying) {
                isFinished = false
                binding.panel.isEnabled = true
                isSetStart = false
                isSetEnd = false
                binding.panel.clear()
                Toast.makeText(this, "Cleared", Toast.LENGTH_SHORT).show()
            }
        }

    }
}