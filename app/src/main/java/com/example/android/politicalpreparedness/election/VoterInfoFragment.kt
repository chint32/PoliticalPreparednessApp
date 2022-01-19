package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.Election

class VoterInfoFragment : Fragment() {

    lateinit var binding: FragmentVoterInfoBinding
    lateinit var viewModel: VoterInfoViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)

        val thisElection = arguments!!.getParcelable<Election>("arg_election")
        Log.d("electionId", thisElection!!.id)
        binding.election = thisElection
        binding.executePendingBindings()

        val viewModelFactory = VoterInfoViewModelFactory(requireActivity().application, binding.election!!)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(VoterInfoViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.viewModel!!.voterInfo.observe(viewLifecycleOwner, Observer {

            val locationLinkedText =
                    String.format("<a href=\"%s\">Voting Locations</a> ", it.state!![0].electionAdministrationBody.votingLocationFinderUrl)

            binding.stateLocations.text = Html.fromHtml(locationLinkedText)
            binding.stateLocations.movementMethod = LinkMovementMethod.getInstance()


            val ballotLinkedText =
                String.format("<a href=\"%s\">Ballot Information</a> ", it.state!![0].electionAdministrationBody.ballotInfoUrl)

            binding.stateBallot.text = Html.fromHtml(ballotLinkedText)
            binding.stateBallot.movementMethod = LinkMovementMethod.getInstance()

        })

        return binding.root
    }
}