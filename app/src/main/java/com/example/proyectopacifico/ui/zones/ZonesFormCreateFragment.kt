package com.example.proyectopacifico.ui.zones

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopacifico.R
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.localdb.AppDatabase
import com.example.proyectopacifico.data.models.entities.*
import com.example.proyectopacifico.data.rest.RetrofitClient
import com.example.proyectopacifico.databinding.DialogCoordinatesBinding
import com.example.proyectopacifico.databinding.DialogPhotoBinding
import com.example.proyectopacifico.databinding.FragmentZonesFormCreateBinding
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
import java.util.*


class ZonesFormCreateFragment : Fragment(R.layout.fragment_zones_form_create) {
    private lateinit var binding: FragmentZonesFormCreateBinding

    private val registerPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (false in it.values) {
                Snackbar.make(
                    binding.root,
                    "You must enable the permissions",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                permissions()
            }
        }

    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val imageBitmap = it?.data?.extras?.get("data") as Bitmap
                bitmap = imageBitmap
                binding.IvZone.setImageBitmap(bitmap)
            }
        }

    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data?.data
            data?.let {
                bitmap = ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        requireContext().contentResolver,
                        Uri.parse(it.toString())
                    )
                )
                binding.IvZone.setImageBitmap(bitmap)
            }
        }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var bitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    private lateinit var departmentList: List<DepartmentEntity>
    private lateinit var ethnicGroupList: List<EthnicGroupEntity>
    private lateinit var municipalitiesList: List<MunicipalityEntity>
    private lateinit var populatedCenterList: List<PopulatedCenterEntity>
    private var ethnicInfo = mutableListOf<EthnicGroupEntity>()
    private lateinit var ethnicAdapter: EthnicGroupAdapter
    private lateinit var selectedEthnicGroup: EthnicGroupEntity
    private lateinit var selectedPopulatedGroup: PopulatedCenterEntity

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
    private val viewModelPopulation by viewModels<PopulationViewModel> {
        PopulationViewModelFactory(
            PopulationRepoImpl(
                AppDatabase.getDatabase(requireContext()).PopulationDao(),
                RetrofitClient.webService
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentZonesFormCreateBinding.bind(view)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        setUpCoordinates()
        obtainDepartments()
        obtainEthnicGroup()
        clicks()
    }

    private fun clicks() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.IvZone.setOnClickListener {
            permissions()
        }
        binding.btnUpdateCoordinates.setOnClickListener { setUpCoordinates() }
        binding.autocompleteSelectDepartments.setOnItemClickListener { parent, view, position, id ->
            obtainMunicipalities(
                position
            )
        }
        binding.autocompleteSelectMunicipalities.setOnItemClickListener { parent, view, position, id ->
            obtainPopulatedCenters(
                position
            )
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
            ethnicAdapter = EthnicGroupAdapter(
                ethnicInfo,
                onClickDelete = { listPosition -> onDeleteEthnicGroup(listPosition) }
            )

            binding.rvEthnicGroup.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            binding.rvEthnicGroup.adapter = ethnicAdapter
        }
        binding.autocompleteSelectVeredas.setOnItemClickListener { parent, view, position, id ->
            selectedPopulatedGroup = populatedCenterList[position]
        }
        binding.btnGuardar.setOnClickListener { validate() }
        binding.btnManualCoordinates.setOnClickListener {
            bottomDialog()
        }
    }

    private fun bottomDialog() {
        val dialog = DialogCoordinatesBinding.inflate(LayoutInflater.from(requireContext()))
        val alert = AlertDialog.Builder(requireContext()).apply {
            setView(dialog.root)
        }.create()

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

    private fun onDeleteEthnicGroup(position: Int) {
        ethnicInfo.removeAt(position)
        ethnicAdapter.notifyItemRemoved(position)
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
        obtainPopulationById()
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
                                    saveData()
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

    private fun saveData() {

        var newByteArray: ByteArray? = null
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

        val bitmap = BitmapFactory.decodeByteArray(
            byteArrayOutputStream.toByteArray(),
            0,
            byteArrayOutputStream.size()
        )
        val resized = Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * 0.8).toInt(),
            (bitmap.height * 0.8).toInt(),
            true
        )
        val stream = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        newByteArray = stream.toByteArray()

        if (newByteArray.size>500000) {
            Snackbar.make(binding.root,"Lo sentimos esta imagenes tiene un tamaño muy grande, debe seleccionar otra",Snackbar.LENGTH_SHORT).show()
            return
        }

        val base64 = Base64.encodeToString(newByteArray, Base64.DEFAULT)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPopulation.savePopulation(
                    PopulationEntity(
                        0,
                        selectedPopulatedGroup.id_populated_center,
                        binding.longitude.text.toString(),
                        binding.latitude.text.toString(),
                        base64,
                        binding.txtInhabitantsNumber.text.toString(),
                    )
                ).collect {
                    when (it) {
                        is Result.Loading -> {
                        }
                        is Result.Success -> {
                            Log.e("saveWebData: ", it.data.toString())
                            saveEthnicGroups(it.data.toString())
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

    private fun saveEthnicGroups(idPopulation: String) {
        ethnicInfo.forEach { ethnic ->
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelPopulationEthnicGroup.savePopulationEthnicGroup(
                        PopulationEthnicGroupEntity(0, ethnic.id_ethnic_group, idPopulation.toInt())
                    ).collect { result ->
                        when (result) {
                            is Result.Loading -> {}
                            is Result.Success -> {
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

    private fun obtainPopulatedCenters(position: Int) {
        val idMunicipality = municipalitiesList[position]
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPopulatedCenter.getDbPopulatedCenters(idMunicipality.id_municipality)
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

    private fun obtainMunicipalities(position: Int) {
        val idDepartment = departmentList[position]
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelMunicipality.getDbDepartments(idDepartment.id_department)
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

    private fun permissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                dialogCameraOrGallery()
            }
            else -> {
                registerPermissions.launch(
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }

    private fun dialogCameraOrGallery() {
        val dialog = DialogPhotoBinding.inflate(LayoutInflater.from(requireContext()))
        val alert = AlertDialog.Builder(requireContext()).apply {
            setView(dialog.root)
        }.create()
        dialog.llCamera.setOnClickListener {
            pickFromCamera()
            alert.dismiss()
        }
        dialog.llGallery.setOnClickListener {
            pickFromGallery()
            alert.dismiss()
        }
        alert.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alert.show()
    }

    private fun pickFromCamera() {
        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResult.launch(i)
    }

    private fun pickFromGallery() {
        val i = Intent(Intent.ACTION_PICK)
        i.type = "image/*"
        galleryResult.launch(i)

    }

}