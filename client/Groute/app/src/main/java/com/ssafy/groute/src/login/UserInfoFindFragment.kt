package com.ssafy.groute.src.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.ssafy.groute.R
import com.ssafy.groute.config.BaseFragment
import com.ssafy.groute.databinding.FragmentUserInfoFindBinding


class UserInfoFindFragment : BaseFragment<FragmentUserInfoFindBinding>(FragmentUserInfoFindBinding::bind, R.layout.fragment_user_info_find) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userInfoFindTabPagerAdapter = UserInfoFindTabPagerAdapter(this)
        val tabList = arrayListOf("아이디 찾기", "비밀번호 변경")

        userInfoFindTabPagerAdapter.addFragment(UserIdFindFragment())
        userInfoFindTabPagerAdapter.addFragment(PasswordChangeFragment())
        binding.userinfoFindVp.adapter =userInfoFindTabPagerAdapter
        TabLayoutMediator(
            binding.userinfoFindTablayout,
            binding.userinfoFindVp
        ) { tab, position ->
            tab.text = tabList.get(position)
        }.attach()

        binding.userinfoFindAppbarIbtnBack.setOnClickListener {
            (requireActivity() as LoginActivity).onBackPressed()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserInfoFindFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}