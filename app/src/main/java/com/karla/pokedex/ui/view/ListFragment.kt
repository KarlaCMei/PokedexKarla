package com.karla.pokedex.ui.view

import android.Manifest
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.karla.pokedex.databinding.FragmentListBinding
import com.karla.pokedex.domain.SelectedListener
import com.karla.pokedex.ui.adapter.ItemAdapter
import com.karla.pokedex.ui.viewmodel.ApiStatus
import com.karla.pokedex.ui.viewmodel.PokeViewModel



class ListFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000
    val UPDATE_FASTEST_INTERVAL_IN_MILLISECONDS: Long = UPDATE_INTERVAL_IN_MILLISECONDS / 2
    val LOCATION_REQUEST: Int = 1
    var locationRequest: LocationRequest? = null
    private var lastLocation: Location? = null

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PokeViewModel by viewModels()
    private lateinit var adapter: ItemAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var listener: SelectedListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as SelectedListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context debe implementar el listener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(it)
        }

        locationRequest = LocationRequest()
            .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
            .setFastestInterval(UPDATE_FASTEST_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setSmallestDisplacement(0f)
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)

        activity?.let {
            val settingsClient = LocationServices.getSettingsClient(it)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(it)
            val locationSettingsRequest = builder.build()
            settingsClient.checkLocationSettings(locationSettingsRequest)
        }

        adapter = ItemAdapter()
        recyclerView = binding.recyclerViewPoke
        recyclerView.adapter = adapter

        binding.buttonGenerate.setOnClickListener{
            generatePokemonRandom()
        }

        observeApiStatus()
        observeListPokemon()
        onClickItem()
        startLocationUpdates()

    }

    private fun observeApiStatus() {
        viewModel.status.observe(viewLifecycleOwner) { status->
            when (status) {
                ApiStatus.LOADING -> {
                    binding.statusOffline.visibility = View.GONE
                    binding.shimmerLoading.visibility = View.VISIBLE
                    binding.recyclerViewPoke.visibility =View.GONE
                }
                ApiStatus.ERROR -> {
                    binding.statusOffline.visibility = View.VISIBLE
                    binding.shimmerLoading.visibility = View.GONE
                    binding.recyclerViewPoke.visibility =View.GONE
                }
                ApiStatus.DONE -> {
                    binding.statusOffline.visibility = View.GONE
                    binding.shimmerLoading.visibility = View.GONE
                    binding.recyclerViewPoke.visibility =View.VISIBLE
                }
            }
        }
    }

    private fun observeListPokemon() {
        viewModel.pokemonList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun onClickItem() {
        adapter.onItemClickListener = { poke ->
            listener.onSelected(poke.id)
        }
    }

    private fun generatePokemonRandom(){
        val listSize = viewModel.pokemonList.value?.size ?: 1
        val random = (1..listSize).random()
        listener.onSelected(random)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun startLocationUpdates() {

        activity?.let {
            if (ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    it, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf<String>(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LOCATION_REQUEST
                )
                return
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }


    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val newLocation = locationResult.lastLocation
            if(lastLocation == null){
                lastLocation = newLocation
            }

            Log.e("M", "LAST LOCATION" + lastLocation)
            Log.e("M", "LAST LOCATION new" + newLocation)
            Log.e("M", "LAST LOCATION distance" + newLocation.distanceTo(lastLocation!!))

            activity?.let {
                if (lastLocation != null && newLocation.distanceTo(lastLocation!!) >= 10) {
                    lastLocation = newLocation

                    // Vibrar y mostrar alerta
                    val v = it.getSystemService(VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                    }else{
                        v.vibrate(500)
                    }

                    Toast.makeText(it,"Pokemon encontrado", Toast.LENGTH_SHORT).show()
                    generatePokemonRandom()
                }
            }

        }
    }



}