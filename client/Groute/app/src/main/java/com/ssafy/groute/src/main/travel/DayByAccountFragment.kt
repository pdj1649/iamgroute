package com.ssafy.groute.src.main.travel

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.groute.R
import com.ssafy.groute.config.BaseFragment
import com.ssafy.groute.databinding.FragmentDayByAccountBinding
import com.ssafy.groute.src.dto.Account
import com.ssafy.groute.src.main.MainActivity
import com.ssafy.groute.src.viewmodel.PlanViewModel
import kotlinx.coroutines.runBlocking

private const val TAG = "DayByAccountFragment"
class DayByAccountFragment : BaseFragment<FragmentDayByAccountBinding>(FragmentDayByAccountBinding::bind, R.layout.fragment_day_by_account) {
    private lateinit var mainActivity: MainActivity
    private var planId = -1
    private val planViewModel: PlanViewModel by activityViewModels()
    private lateinit var accountInAdapter:AccountInAdapter
    private lateinit var accountOutAdapter : AccountOutAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        arguments?.let {
            planId = it.getInt("planId",-1)
        }
        Log.d(TAG, "onAttach: ${planId}")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runBlocking {
            planViewModel.getAccountList(planId)
        }
        initOutAdapter()
    }
    fun initOutAdapter(){
        planViewModel.accountList.observe(viewLifecycleOwner, Observer {
            accountOutAdapter = AccountOutAdapter(requireContext())
            accountOutAdapter.list = it
            binding.accountRvList.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
                adapter = accountOutAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        })

    }
    companion object {

        fun newInstance(key: String, value: Int) =
            DayByAccountFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }
}