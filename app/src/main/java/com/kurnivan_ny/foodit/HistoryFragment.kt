package com.kurnivan_ny.foodit

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.*
import com.kurnivan_ny.foodit.databinding.FragmentHistoryBinding
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var db: FirebaseFirestore

    private var historyList: ArrayList<ListHistoryModel> = arrayListOf()
    private var historyListAdapter: ListHistoryAdapter = ListHistoryAdapter(historyList)

    private lateinit var viewModel: HistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = SharedPreferences(requireContext())
        db = FirebaseFirestore.getInstance()

        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        val bulan = resources.getStringArray(R.array.bulan)
        val arrayAdapterBulan = ArrayAdapter(requireContext(), R.layout.dropdown_item, bulan)
        binding.edtBulan.setAdapter(arrayAdapterBulan)

        binding.rvHistory.setHasFixedSize(true)
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())

        viewModel.newhistory.observe(viewLifecycleOwner, Observer {
            historyListAdapter.historyList = it
            historyListAdapter.notifyDataSetChanged()
        })

        binding.rvHistory.adapter = historyListAdapter

        binding.rvHistory.itemAnimator = SlideInUpAnimator()

        binding.edtBulan.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String

            getHistory(selectedItem)
        }
    }

    private fun getHistory(selectedItem: String) {
        val username = sharedPreferences.getValuesString("username")

        db.collection("users").document(username!!)
            .collection(selectedItem).orderBy("tanggal_makan", Query.Direction.DESCENDING)
            .addSnapshotListener(object: EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?) {
                    if (error != null){
                        Log.e("Firestore Error", error.message.toString())
                    }
                    val doc = arrayListOf<ListHistoryModel>()
                    if(value != null){
                        for (dc in value.documents){
                            val data = dc.toObject(ListHistoryModel::class.java)
                            if (data != null){
                                doc.add(data)
                            }
                        }
                    }
                    viewModel.newhistory.value = doc
                    historyListAdapter.notifyDataSetChanged()
                }

            })
    }
}