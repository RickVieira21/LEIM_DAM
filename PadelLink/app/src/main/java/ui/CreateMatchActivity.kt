package ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.padellink.R
import com.example.padellink.databinding.ActivityCreateMatchBinding
import ui.viewModel.CreateMatchViewModel
import java.util.*

class CreateMatchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateMatchBinding
    private val viewModel: CreateMatchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_match)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupSpinner()
        setupDateTimePickers()

        binding.buttonCreateMatch.setOnClickListener {
            viewModel.createMatch()
        }

        viewModel.navigateToHome.observe(this, { navigate ->
            if (navigate) {
                finish()
            }
        })

        val spinnerMatchLevel: Spinner = findViewById(R.id.spinnerMatchLevel)
        val matchLevel = resources.getStringArray(R.array.match_levels)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, matchLevel)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMatchLevel.adapter = adapter

        // Listener para capturar o item selecionado
        spinnerMatchLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLevel = parent.getItemAtPosition(position) as String
                viewModel.matchLevel.value = selectedLevel
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Fazer algo se nenhum item for selecionado (opcional)
            }
        }

    }

    private fun setupSpinner() {
        val levels = resources.getStringArray(R.array.match_levels)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, levels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMatchLevel.adapter = adapter
    }

    private fun setupDateTimePickers() {
        binding.editTextMatchDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val date = "$dayOfMonth/${month + 1}/$year"
                viewModel.matchDate.value = date
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }

        binding.editTextMatchStartingTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
                val time = "$hourOfDay:$minute"
                viewModel.matchStartingTime.value = time
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            timePickerDialog.show()
        }
    }
}
