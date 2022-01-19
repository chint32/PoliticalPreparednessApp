package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import java.util.*


class DetailFragment : Fragment() {

    companion object {
        private val REQUEST_LOCATION_PERMISSION = 1
    }

    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var mfusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: RepresentativeViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)

        val viewModelFactory = RepresentativeViewModelFactory()
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(RepresentativeViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        mfusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.buttonSearch.setOnClickListener(View.OnClickListener {
            val address = Address(
                binding.addressLine1.text.toString(),
                binding.addressLine2.text.toString(),
                binding.city.text.toString(),
                binding.state.selectedItem.toString(),
                binding.zip.text.toString()
            )
            binding.viewModel!!.getRepresentivesForAddress(address.toFormattedString())
        })

        binding.buttonLocation.setOnClickListener(View.OnClickListener {
            if (isPermissionGranted())
                getLocation()
            else checkLocationPermissions()

        })


        val adapter = RepresentativeListAdapter(RepresentativeListAdapter.OnClickListener {

        })

        binding.representativesRecycler.adapter = adapter

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) === PackageManager.PERMISSION_GRANTED
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (isPermissionGranted()) {
            if (checkLocationPermissions()) {
                mfusedLocationClient.lastLocation.addOnCompleteListener(
                    OnCompleteListener<Location?> { task ->
                        val location: Location = task.result!!
                        val address = geoCodeLocation(location)
                        binding.addressLine1.setText(address.line2 + " " + address.line1, TextView.BufferType.EDITABLE)
//                        binding.addressLine2.setText(address.line2, TextView.BufferType.EDITABLE)
                        binding.city.setText(address.city, TextView.BufferType.EDITABLE)
                        binding.zip.setText(address.zip, TextView.BufferType.EDITABLE)
                        val allStates = resources.obtainTypedArray(R.array.states)
                        for(i in 0..allStates.length()){
                            if(address.state == allStates.getString(i))
                                binding.state.setSelection(i)
                        }
                        binding.viewModel!!.getRepresentivesForAddress(address.toFormattedString())
                    }
                )
            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_LONG).show()

                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }


    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}