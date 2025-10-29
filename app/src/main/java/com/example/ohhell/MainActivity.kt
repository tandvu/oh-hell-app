package com.example.ohhell

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.graphics.Color
import com.google.android.material.textfield.TextInputLayout
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ohhell.adapter.PlayersAdapter
import com.example.ohhell.databinding.ActivityMainBinding
import com.example.ohhell.model.GameState

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val gameState = GameState()
    // Names loaded only from res/raw/player_names.txt; we will not persist user-entered names.
    private lateinit var savedPlayerNames: MutableSet<String>
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>
    private lateinit var seatAdapters: MutableList<ArrayAdapter<String>>
    private lateinit var seats: List<AutoCompleteTextView>
    private lateinit var seatRows: List<View>
    // Track selected raw names per seat (null = empty)
    private lateinit var selectedNames: MutableList<String?>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        loadSavedPlayerNames()
        setupAutoComplete()
        setupSeatListeners()
        setupClickListeners()
        updateStartButtonState()
    }
    
    private fun loadSavedPlayerNames() {
        // Only load from the raw resource list. Do not read SharedPreferences.
        savedPlayerNames = mutableSetOf()
        try {
            val inputStream = resources.openRawResource(R.raw.player_names)
            inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val name = line.trim()
                    if (name.isNotEmpty()) savedPlayerNames.add(name)
                }
            }
        } catch (e: Exception) {
            // If the resource is missing or unreadable, leave the set empty.
        }
    }
    
    private fun savePlayerName(name: String) {
        // Intentionally left blank: we do not persist names between sessions per user request.
        // This is kept as a no-op so existing call-sites don't need to change.
    }
    
    private fun setupAutoComplete() {
        val playerNamesList = savedPlayerNames.toList().sorted()
        // Use a custom dropdown layout that forces black text and make sure the backing list is mutable.
        seats = listOf(
            binding.seat1,
            binding.seat2,
            binding.seat3,
            binding.seat4,
            binding.seat5,
            binding.seat6,
            binding.seat7,
            binding.seat8,
        )
        // Row containers corresponding to each seat (label + input). These IDs were added to the layout.
        seatRows = listOf(
            binding.rowSeat1,
            binding.rowSeat2,
            binding.rowSeat3,
            binding.rowSeat4,
            binding.rowSeat5,
            binding.rowSeat6,
            binding.rowSeat7,
            binding.rowSeat8,
        )
        // Create adapters and initialize selection state
        seatAdapters = mutableListOf()
        selectedNames = MutableList(seats.size) { null }
        seats.forEachIndexed { index, seat ->
            val adapter = buildAdapterForSeat(index)
            seatAdapters.add(adapter)
            seat.setAdapter(adapter)
            seat.threshold = 1
            // At start only the first seat's row (LinearLayout containing label + input) is visible
            seatRows.getOrNull(index)?.visibility = if (index == 0) View.VISIBLE else View.GONE
        }
    }

    // Build a per-seat adapter where items already selected in other seats are shown but disabled
    private fun buildAdapterForSeat(seatIndex: Int): ArrayAdapter<String> {
    // Adapter items come only from the raw resource list (no persisted or user-saved names).
    val names = savedPlayerNames.toList().sorted()
        val display = ArrayList<String>()
        val enabled = ArrayList<Boolean>()

        names.forEach { name ->
            val at = selectedNames.indexOfFirst { it != null && it.equals(name, ignoreCase = true) }
            if (at >= 0 && at != seatIndex) {
                display.add("$name (seat ${at + 1})")
                enabled.add(false)
            } else {
                display.add(name)
                enabled.add(true)
            }
        }

        return object : ArrayAdapter<String>(this, R.layout.dropdown_item, display) {
            override fun isEnabled(position: Int): Boolean = enabled.getOrElse(position) { true }

            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val v = super.getDropDownView(position, convertView, parent)
                val tv = v.findViewById<android.widget.TextView>(android.R.id.text1)
                if (!isEnabled(position)) tv.setTextColor(Color.GRAY) else tv.setTextColor(Color.BLACK)
                return v
            }
        }
    }
    
    private fun updateAutoCompleteAdapter() {
        // Recreate adapters for each seat so disabled/annotated entries reflect current selections.
        seatAdapters.clear()
        seats.forEachIndexed { index, seat ->
            val adapter = buildAdapterForSeat(index)
            seatAdapters.add(adapter)
            seat.setAdapter(adapter)
        }
    }
    
    private fun setupRecyclerView() {
        // Recycler view removed — seat dropdowns are used instead.
    }
    
    private fun setupClickListeners() {
        binding.btnStartGame.setOnClickListener {
            startGame()
        }
    }
    
    private fun addPlayer() {
        // Deprecated — replaced with seat-based selection UI.
    }
    
    private fun startGame() {
        // Collect selected unique player names from seats (strip any dealer tag)
        val selected = linkedSetOf<String>()
        seats.forEach { seat ->
            val raw = seat.text.toString().trim().removeSuffix(" (dealer)").trim()
            if (raw.isNotEmpty()) selected.add(raw)
        }

        if (selected.size < 2) {
            Toast.makeText(this, R.string.minimum_2_players, Toast.LENGTH_SHORT).show()
            return
        }

        // Reset game state and add selected players
        gameState.reset()
        selected.forEach { gameState.addPlayer(it) }

        gameState.startGame()
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }
    
    private fun updateUI() {
        // Update start button state based on seats (7 or 8 players required to enable)
        updateStartButtonState()
    }

    private fun setupSeatListeners() {
        // Update UI when seat selections change
        seats.forEachIndexed { index, seat ->
            val watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                        // Update selection state and reveal/hide following seats
                        val displayName = s?.toString()?.trim() ?: ""
                        if (displayName.isNotEmpty()) {
                            // Prevent duplicate names across seats
                            val usedAt = selectedNames.indexOfFirst { it != null && it.equals(displayName, ignoreCase = true) }
                                if (usedAt >= 0 && usedAt != index) {
                                Toast.makeText(this@MainActivity, "Name already used: $displayName", Toast.LENGTH_SHORT).show()
                                // revert the change
                                seat.removeTextChangedListener(this)
                                seat.setText("")
                                seat.addTextChangedListener(this)
                                return
                            }
                            selectedNames[index] = displayName
                            savePlayerName(displayName)
                                // reveal next seat row (do not auto-focus)
                                seatRows.getOrNull(index + 1)?.visibility = View.VISIBLE
                        } else {
                            // If this seat was cleared, clear selection and hide/clear all following seats.
                            selectedNames[index] = null
                            for (i in index + 1 until seats.size) {
                                val following = seats[i]
                                following.setText("")
                                seatRows.getOrNull(i)?.visibility = View.GONE
                            }
                        }
                    // refresh adapters to show disabled items correctly
                    updateAutoCompleteAdapter()
                    updateStartButtonState()
                }
            }

            seat.addTextChangedListener(watcher)

            seat.setOnItemClickListener { parent, view, position, id ->
                // When an item is picked from suggestions
                val pickedDisplay = (parent.getItemAtPosition(position) as? String ?: return@setOnItemClickListener)
                // strip any " (seat N)" annotation to get canonical name
                val pickedRaw = pickedDisplay.replace(Regex("\\s*\\(seat \\d+\\)\\s*$"), "").trim()

                // If the picked item is already used by another seat, prevent selection
                val alreadyAt = selectedNames.indexOfFirst { it != null && it.equals(pickedRaw, ignoreCase = true) }
                if (alreadyAt >= 0 && alreadyAt != index) {
                    // shouldn't happen because adapter disables these items, but guard anyway
                    Toast.makeText(this, "Name already used: $pickedRaw", Toast.LENGTH_SHORT).show()
                    return@setOnItemClickListener
                }

                // Save and display seat text (just the name). Don't prefix with seat number.
                selectedNames[index] = pickedRaw
                savePlayerName(pickedRaw)
                seat.removeTextChangedListener(watcher)
                seat.setText(pickedRaw)
                seat.setSelection(seat.text.length)
                seat.addTextChangedListener(watcher)

                // Recreate adapters so other seats show this selection as disabled
                updateAutoCompleteAdapter()

                // Reveal the next seat row but do NOT auto-focus it
                seatRows.getOrNull(index + 1)?.visibility = View.VISIBLE

                seat.clearFocus()
                seat.dismissDropDown()

                updateStartButtonState()
            }

            
        }
    }

    private fun updateStartButtonState() {
        val selected = mutableSetOf<String>()
        seats.forEach { seat ->
            val name = seat.text.toString().trim().removeSuffix(" (dealer)").trim()
            if (name.isNotEmpty()) selected.add(name)
        }
        // Enable Start when at least 7 unique names are selected (or 8)
        binding.btnStartGame.isEnabled = selected.size >= 7
    }
    
    companion object {
        val gameState = GameState()
    }
}