package com.sapuseven.untis.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sapuseven.untis.R
import com.sapuseven.untis.adapters.AbsenceCheckAdapter
import com.sapuseven.untis.adapters.AbsenceCheckAdapterItem
import com.sapuseven.untis.data.databases.UserDatabase
import com.sapuseven.untis.models.untis.timetable.Period
import com.sapuseven.untis.viewmodels.AbsenceCheckViewModel
import java.text.Collator

class AbsenceCheckFragment(user: UserDatabase.User?, element: Period?) : Fragment() {
	constructor() : this(null, null)

	private val viewModel by viewModels<AbsenceCheckViewModel> { AbsenceCheckViewModel.Factory(user, element) }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val rootView = activity?.layoutInflater?.inflate(R.layout.fragment_timetable_absence_check, container, false) as ViewGroup

		val rvAbsenceCheck = rootView.findViewById<RecyclerView>(R.id.recyclerview_absence_check)
		val adapter = AbsenceCheckAdapter {
			viewModel.createAbsence(it.student)
		}
		rvAbsenceCheck.layoutManager = LinearLayoutManager(context)
		rvAbsenceCheck.adapter = adapter

		viewModel.absenceList().observe(viewLifecycleOwner, Observer { absenceList ->
			rootView.findViewById<ProgressBar>(R.id.progressbar_absencecheck_loading).visibility = View.GONE
			adapter.clear()
			adapter.addItems(absenceList.map { AbsenceCheckAdapterItem(it.key, it.value) }.sortedWith(Comparator { s1, s2 ->
				Collator.getInstance().compare(s1.student.fullName(), s2.student.fullName())
			}))
			adapter.notifyDataSetChanged()
		})

		return rootView
	}
}
