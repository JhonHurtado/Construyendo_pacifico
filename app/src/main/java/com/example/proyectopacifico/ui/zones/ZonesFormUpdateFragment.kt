package com.example.proyectopacifico.ui.zones

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopacifico.R
import com.example.proyectopacifico.core.Constants
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.localdb.AppDatabase
import com.example.proyectopacifico.data.models.entities.*
import com.example.proyectopacifico.data.rest.RetrofitClient
import com.example.proyectopacifico.databinding.DialogCoordinatesBinding
import com.example.proyectopacifico.databinding.FragmentZonesFormUpdateBinding
import com.example.proyectopacifico.domain.department.DepartmentRepoImpl
import com.example.proyectopacifico.domain.ethnicGroup.EthnicGroupRepoImpl
import com.example.proyectopacifico.domain.municipality.MunicipalityRepoImpl
import com.example.proyectopacifico.domain.populatedCenter.PopulatedCenterRepoImpl
import com.example.proyectopacifico.domain.population.PopulationRepoImpl
import com.example.proyectopacifico.domain.populationEthnicGroup.PopulationEthnicGroupRepoImpl
import com.example.proyectopacifico.presentation.*
import com.example.proyectopacifico.ui.zones.adapter.EthnicGroupAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.io.ByteArrayOutputStream


class ZonesFormUpdateFragment : Fragment(R.layout.fragment_zones_form_update) {
    private lateinit var binding: FragmentZonesFormUpdateBinding
    private val args by navArgs<ZonesFormUpdateFragmentArgs>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var bitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)


    private val viewModelDepartment by viewModels<DepartmentViewModel> {
        DepartmentViewModelFactory(
            DepartmentRepoImpl(
                AppDatabase.getDatabase(requireContext()).DepartmentDao()
            )
        )
    }
    private val viewModelMunicipality by viewModels<MunicipalityViewModel> {
        MunicipalityViewModelFactory(
            MunicipalityRepoImpl(
                AppDatabase.getDatabase(requireContext()).MunicipalityDao()
            )
        )
    }
    private val viewModelPopulatedCenter by viewModels<PopulatedCenterViewModel> {
        PopulatedCenterViewModelFactory(
            PopulatedCenterRepoImpl(
                AppDatabase.getDatabase(requireContext()).PopulatedCenterDao()
            )
        )
    }
    private val viewModelEthnicGroup by viewModels<EthnicGroupViewModel> {
        EthnicGroupViewModelFactory(
            EthnicGroupRepoImpl(
                AppDatabase.getDatabase(requireContext()).EthnicGroupDao()
            )
        )
    }
    private val viewModelPopulationEthnicGroup by viewModels<PopulationEthnicGroupViewModel> {
        PopulationEthnicGroupViewModelFactory(
            PopulationEthnicGroupRepoImpl(
                AppDatabase.getDatabase(requireContext()).PopulationEthnicGroupDao()
            )
        )
    }
    private val viewModelPopulation by viewModels<PopulationViewModel> {
        PopulationViewModelFactory(
            PopulationRepoImpl(
                AppDatabase.getDatabase(requireContext()).PopulationDao(),
                RetrofitClient.webService
            )
        )
    }


    private lateinit var departmentList: List<DepartmentEntity>
    private lateinit var municipalitiesList: List<MunicipalityEntity>
    private lateinit var populatedCenterList: List<PopulatedCenterEntity>
    private lateinit var ethnicGroupList: List<EthnicGroupEntity>
    private var ethnicInfo = mutableListOf<EthnicGroupEntity>()
    private lateinit var ethnicAdapter: EthnicGroupAdapter
    private lateinit var selectedEthnicGroup: EthnicGroupEntity
    private lateinit var selectedPopulatedGroup: PopulatedCenterEntity


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentZonesFormUpdateBinding.bind(view)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        Log.e("bool: ", "hksdhfs")
        obtainDepartments()
        obtainEthnicGroup()
        referenceData()
        clicks()
    }

    private fun clicks() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnUpdateCoordinates.setOnClickListener { setUpCoordinates() }
        binding.autocompleteSelectDepartments.setOnItemClickListener { parent, view, position, id ->
            val department = departmentList[position]
            obtainMunicipalities(
                department.id_department
            )
            binding.autocompleteSelectMunicipalities.setText("")
            binding.autocompleteSelectVeredas.setText("")
        }
        binding.autocompleteSelectMunicipalities.setOnItemClickListener { parent, view, position, id ->
            val municipality = municipalitiesList[position]
            obtainPopulatedCenters(
                municipality.id_municipality
            )
        }
        binding.autocompleteSelectVeredas.setOnItemClickListener { parent, view, position, id ->
            selectedPopulatedGroup = populatedCenterList[position]
        }
        binding.autocompleteSelectEthnicGroup.setOnItemClickListener { parent, view, position, id ->
            selectedEthnicGroup = ethnicGroupList[position]

            if (ethnicInfo.contains(ethnicGroupList[position])) {
                Snackbar.make(
                    binding.root,
                    "Este grupo etnico ya se encuentra agregado",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                ethnicInfo.add(ethnicGroupList[position])
            }
            ethnicAdapter = EthnicGroupAdapter(ethnicInfo, onClickDelete = { listPosition -> onDeleteEthnicGroup(listPosition) })

            binding.rvEthnicGroup.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            binding.rvEthnicGroup.adapter = ethnicAdapter
        }
        binding.btnUpdate.setOnClickListener { validate() }
        binding.btnManualCoordinates.setOnClickListener {
            bottomDialog()
        }
    }

    private fun bottomDialog() {
        val dialog = DialogCoordinatesBinding.inflate(LayoutInflater.from(requireContext()))
        val alert = AlertDialog.Builder(requireContext()).apply {
            setView(dialog.root)
        }.create()

        dialog.textView.text = "Actualiza tus Coordenadas"
        dialog.btnSave.setOnClickListener {
            validateDialogCoordinates(dialog, alert)
        }


        alert.window?.setBackgroundDrawableResource(R.color.transparent)
        alert.show()
    }

    private fun validateDialogCoordinates(dialog: DialogCoordinatesBinding, alert: AlertDialog) {
        val res = arrayOf(validateEmpty(dialog.txtLongitude), validateEmpty(dialog.txtLatitude))
        if (false in res) return

        updateInfo(dialog, alert)

    }

    private fun updateInfo(dialog: DialogCoordinatesBinding, alert: AlertDialog) {
        binding.longitude.text = dialog.txtLongitude.text
        binding.latitude.text = dialog.txtLatitude.text
        alert.dismiss()
    }

    private fun validateEmpty(edt: TextInputEditText): Boolean {
        return if (edt.text.toString().isNullOrEmpty()) {
            edt.error = "Este campo es obligatorio"
            false
        } else {
            edt.error = null
            true
        }
    }

    private fun validate() {
        val results = arrayOf(
            validateDepartment(),
            validateMunicipality(),
            validatePopulatedCenter(),
            validateEthnicGroup(),
            validateInhabitantsNumber(),
            validateCoordinates(binding.longitude),
            validateCoordinates(binding.latitude)
        )
        if (false in results) {
            return
        }
        if (args.population.id_populated_center != selectedPopulatedGroup.id_populated_center) obtainPopulationById() else updateData()
    }

    private fun validateCoordinates(tv: TextView): Boolean {
        return if (tv.text.toString() == "0") {
            tv.error = "No puede ser 0"
            false
        } else {
            tv.error = null
            true
        }
    }

    private fun obtainPopulationById() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPopulation.getPopulationByPopulatedCenter(selectedPopulatedGroup.id_populated_center)
                    .collect {
                        when (it) {
                            is Result.Loading -> {
                            }
                            is Result.Success -> {
                                if (it.data == null) {
                                    updateData()
                                } else {
                                    Snackbar.make(
                                        binding.root,
                                        "Esta poblacion ya se encuentra agregada",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is Result.Failure -> {
                                Log.e("Error", "obtainPopulationById: ${it.exception}")
                            }
                        }
                    }
            }
        }
    }

    private fun updateData() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPopulation.updatePopulation(
                    PopulationEntity(
                        args.population.id_population,
                        selectedPopulatedGroup.id_populated_center,
                        binding.longitude.text.toString(),
                        binding.latitude.text.toString(),
                        args.population.photography,
                        binding.txtInhabitantsNumber.text.toString(),
                    )
                ).collect {
                    when (it) {
                        is Result.Loading -> {
                        }
                        is Result.Success -> {
                            deleteEthnicGroupsFromBd()
                        }
                        is Result.Failure -> {
                            Snackbar.make(
                                binding.root,
                                "Error al registrarse",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Error", "sendUser: ${it.exception}")
                        }
                    }
                }
            }
        }
    }

    private fun deleteEthnicGroupsFromBd() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPopulationEthnicGroup.deleteEthnicGroupsByPopulationId(args.population.id_population)
                    .collect {
                        when (it) {
                            is Result.Loading -> {
                            }
                            is Result.Success -> {
                                saveEthnicGroups()
                            }
                            is Result.Failure -> {
                                Snackbar.make(
                                    binding.root,
                                    "Error al registrarse",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                Log.e("Error", "sendUser: ${it.exception}")
                            }
                        }
                    }
            }
        }
    }

    private fun saveEthnicGroups() {
        ethnicInfo.forEach { ethnic ->
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelPopulationEthnicGroup.savePopulationEthnicGroup(
                        PopulationEthnicGroupEntity(
                            0,
                            ethnic.id_ethnic_group,
                            args.population.id_population
                        )
                    ).collect { result ->
                        when (result) {
                            is Result.Loading -> {}
                            is Result.Success ->  {
                                Log.e("saveWebData: ", result.data.toString())

                            }
                            is Result.Failure -> {
                                Snackbar.make(
                                    binding.root,
                                    "Error al Guardar los grupos ethnicos",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                Log.e("Error", "sendUser: ${result.exception}")
                            }
                        }
                    }
                }
            }
        }
        findNavController().popBackStack()
    }

    private fun validateDepartment(): Boolean {
        return if (binding.autocompleteSelectDepartments!!.text.isNullOrEmpty()) {
            binding.autocompleteSelectDepartments!!.error = "Este campo es obligatorio"
            false
        } else {
            binding.autocompleteSelectDepartments!!.error = null
            true
        }
    }

    private fun validateMunicipality(): Boolean {
        return if (binding.autocompleteSelectMunicipalities.text.toString().isNullOrEmpty()) {
            binding.autocompleteSelectMunicipalities.error = "Este campo es obligatorio"
            false
        } else {
            binding.autocompleteSelectMunicipalities.error = null
            true
        }
    }

    private fun validatePopulatedCenter(): Boolean {
        return if (binding.autocompleteSelectVeredas.text.toString().isNullOrEmpty()) {
            binding.autocompleteSelectVeredas.error = "Este campo es obligatorio"
            false
        } else {
            binding.autocompleteSelectVeredas.error = null
            true
        }
    }

    private fun validateEthnicGroup(): Boolean {
        return if (ethnicInfo.isEmpty()) {
            binding.autocompleteSelectEthnicGroup.error = "Este campo es obligatorio"
            false
        } else {
            binding.autocompleteSelectEthnicGroup.error = null
            true
        }
    }

    private fun validateInhabitantsNumber(): Boolean {
        return if (binding.txtInhabitantsNumber?.text.toString().isEmpty()) {
            binding.txtILInhabitantsNumber!!.error = "Este campo es obligatorio"
            false
        } else {
            binding.txtILInhabitantsNumber!!.error = null
            true
        }
    }

    private fun referenceData() {
        binding.longitude.text = args.population.longitude
        binding.latitude.text = args.population.latitude
        setImage(args.population.photography)
        binding.txtInhabitantsNumber.setText(args.population.inhabitants_number)
        binding.autocompleteSelectDepartments.setText(args.population.department_name)
        binding.autocompleteSelectMunicipalities.setText(args.population.municipality_name)
        binding.autocompleteSelectVeredas.setText(args.population.populated_center_name)
        selectedPopulatedGroup = PopulatedCenterEntity(
            args.population.id_populated_center,
            args.population.municipality_id,
            args.population.populated_center_name,
            args.population.populated_center_type
        )
        obtainMunicipalities(args.population.id_department)
        obtainPopulatedCenters(args.population.id_municipality)
        obtainPopulationEthnic()
    }

    private fun setImage(photography: String) {
        val decodedString: ByteArray = Base64.decode(photography, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        binding.IvZone.setImageBitmap(decodedByte)
    }

    private fun obtainPopulationEthnic() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPopulationEthnicGroup.getEthnicGroupsByPopulationId(args.population.id_population)
                    .collect {
                        when (it) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                it.data.forEach {
                                    ethnicInfo.add(
                                        EthnicGroupEntity(
                                            it.id_ethnic_group,
                                            it.ethnic_group_name
                                        )
                                    )
                                }
                                setAdapterEthnic()
                            }
                            is Result.Failure -> {
                                Snackbar.make(
                                    binding.root,
                                    "Error al obtener datos",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                Log.e("Error", "sendUser: ${it.exception}")
                            }
                        }
                    }
            }
        }
    }

    private fun setAdapterEthnic() {
        ethnicAdapter = EthnicGroupAdapter(
            ethnicInfo,
            onClickDelete = { listPosition -> onDeleteEthnicGroup(listPosition) })

        binding.rvEthnicGroup.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvEthnicGroup.adapter = ethnicAdapter
    }

    private fun onDeleteEthnicGroup(position: Int) {
        ethnicInfo.removeAt(position)
        ethnicAdapter.notifyItemRemoved(position)
    }

    private fun obtainDepartments() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelDepartment.getDbDepartments().collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            val departments: MutableList<String> = mutableListOf()
                            departmentList = it.data
                            it.data.forEach {
                                departments.add(it.department_name)
                            }

                            setUpDepartments(departments)
                        }
                        is Result.Failure -> {
                            Snackbar.make(
                                binding.root,
                                "Error al obtener los datos",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Error", "obtainAbilities: ${it.exception.message}")
                        }
                    }
                }
            }
        }
    }

    private fun setUpDepartments(departments: MutableList<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, departments)
        binding.autocompleteSelectDepartments.setAdapter(adapter)
    }

    private fun obtainMunicipalities(idDepartment: Int) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelMunicipality.getDbDepartments(idDepartment)
                    .collect {
                        when (it) {
                            is Result.Loading -> {}
                            is Result.Success -> {

                                val municipalities: MutableList<String> = mutableListOf()
                                municipalitiesList = it.data
                                it.data.forEach {
                                    municipalities.add(it.municipality_name)
                                }

                                setUpMunicipalities(municipalities)
                            }
                            is Result.Failure -> {
                                Snackbar.make(
                                    binding.root,
                                    "Error al obtener los datos",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                Log.e("Error", "obtainAbilities: ${it.exception.message}")
                            }
                        }
                    }
            }
        }
    }

    private fun setUpMunicipalities(municipalities: MutableList<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, municipalities)
        binding.autocompleteSelectMunicipalities?.setAdapter(adapter)
    }

    private fun obtainPopulatedCenters(idMunicipality: Int) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPopulatedCenter.getDbPopulatedCenters(idMunicipality)
                    .collect {
                        when (it) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                val populatedCenters: MutableList<String> = mutableListOf()
                                populatedCenterList = it.data
                                it.data.forEach {
                                    populatedCenters.add(it.populated_center_name)
                                }

                                setUpPopulatedCenters(populatedCenters)
                            }
                            is Result.Failure -> {
                                Snackbar.make(
                                    binding.root,
                                    "Error al obtener los datos",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                Log.e("Error", "obtainAbilities: ${it.exception.message}")
                            }
                        }
                    }
            }
        }
    }

    private fun setUpPopulatedCenters(populatedCenters: MutableList<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, populatedCenters)
        binding.autocompleteSelectVeredas?.setAdapter(adapter)
    }

    private fun obtainEthnicGroup() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelEthnicGroup.getDbEthnicGroups().collect {
                    when (it) {
                        is Result.Loading -> {
                        }
                        is Result.Success -> {
                            val ethnicGroups: MutableList<String> = mutableListOf()
                            ethnicGroupList = it.data
                            it.data.forEach { e ->
                                ethnicGroups.add(e.ethnic_group_name)
                            }

                            setUpEthnicGroup(ethnicGroups)
                        }
                        is Result.Failure -> {
                            Snackbar.make(
                                binding.root,
                                "Error al obtener los datos",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Error", "obtainAbilities: ${it.exception.message}")
                        }
                    }
                }
            }
        }
    }

    private fun setUpEthnicGroup(ethnic: MutableList<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, ethnic)
        binding.autocompleteSelectEthnicGroup.setAdapter(adapter)
    }

    private fun setUpCoordinates() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
        } else {
            getLocations()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocations() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it == null) {
                Toast.makeText(
                    requireContext(),
                    "no se puede obtener la locacion",
                    Toast.LENGTH_SHORT
                ).show()
            } else it.apply {
                binding.latitude.text = it.latitude.toString()
                binding.longitude.text = it.longitude.toString()
            }
        }
    }

}