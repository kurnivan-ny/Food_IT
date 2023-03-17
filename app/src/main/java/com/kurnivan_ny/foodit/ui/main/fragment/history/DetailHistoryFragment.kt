package com.kurnivan_ny.foodit.ui.main.fragment.history

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.firestore.*
import com.kurnivan_ny.foodit.R
import com.kurnivan_ny.foodit.data.model.history.ListDetailHistoryModel
import com.kurnivan_ny.foodit.databinding.FragmentDetailHistoryBinding
import com.kurnivan_ny.foodit.ui.adapter.ListDetailKarbohidratHistoryAdapter
import com.kurnivan_ny.foodit.ui.adapter.ListDetailLemakHistoryAdapter
import com.kurnivan_ny.foodit.ui.adapter.ListDetailProteinHistoryAdapter
import com.kurnivan_ny.foodit.viewmodel.preferences.SharedPreferences
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class DetailHistoryFragment : Fragment() {

    private var _binding: FragmentDetailHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var db: FirebaseFirestore

    private lateinit var pieChartKarbohidrat: PieChart
    private lateinit var pieChartProtein: PieChart
    private lateinit var pieChartLemak: PieChart

    private var detailhistoryList: ArrayList<ListDetailHistoryModel> = arrayListOf()

    private var detailhistorykarbohidratpagiListAdapter: ListDetailKarbohidratHistoryAdapter =
        ListDetailKarbohidratHistoryAdapter(detailhistoryList)
    private var detailhistorykarbohidratsiangListAdapter: ListDetailKarbohidratHistoryAdapter =
        ListDetailKarbohidratHistoryAdapter(detailhistoryList)
    private var detailhistorykarbohidratmalamListAdapter: ListDetailKarbohidratHistoryAdapter =
        ListDetailKarbohidratHistoryAdapter(detailhistoryList)

    private var detailhistoryproteinpagiListAdapter: ListDetailProteinHistoryAdapter =
        ListDetailProteinHistoryAdapter(detailhistoryList)
    private var detailhistoryproteinsiangListAdapter: ListDetailProteinHistoryAdapter =
        ListDetailProteinHistoryAdapter(detailhistoryList)
    private var detailhistoryproteinmalamListAdapter: ListDetailProteinHistoryAdapter =
        ListDetailProteinHistoryAdapter(detailhistoryList)

    private var detailhistorylemakpagiListAdapter: ListDetailLemakHistoryAdapter =
        ListDetailLemakHistoryAdapter(detailhistoryList)
    private var detailhistorylemaksiangListAdapter: ListDetailLemakHistoryAdapter =
        ListDetailLemakHistoryAdapter(detailhistoryList)
    private var detailhistorylemakmalamListAdapter: ListDetailLemakHistoryAdapter =
        ListDetailLemakHistoryAdapter(detailhistoryList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailHistoryBinding.inflate(inflater, container, false)
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

        val username = sharedPreferences.getValuesString("username")
        val tanggal_makan =  sharedPreferences.getValuesString("tanggal_makan_sekarang")
        val bulan_makan =  sharedPreferences.getValuesString("bulan_makan_sekarang")
//        val tanggal_makan = intent.getStringExtra("tanggal_makan").toString()
//        val bulan_makan = intent.getStringExtra("bulan_makan").toString()

        binding.tvTitle.setText(tanggal_makan)

        binding.ivBack.setOnClickListener {
//            val intent = Intent(this@DetailHistoryActivity, HomeActivity::class.java)
//            startActivity(intent)
//            finishAffinity()

            val toHistoryFragment = DetailHistoryFragmentDirections.actionDetailHistoryFragmentToHistoryFragment()
            binding.root.findNavController().navigate(toHistoryFragment)
        }

        binding.ivDelete.setOnClickListener {

            // delete data
            db.collection("users").document(username!!)
                .collection(bulan_makan!!).document(tanggal_makan!!)
                .delete()

//            val intent = Intent(this@DetailHistoryActivity, HomeActivity::class.java)
//            startActivity(intent)
//            finishAffinity()
            val toHistoryFragment = DetailHistoryFragmentDirections.actionDetailHistoryFragmentToHistoryFragment()
            binding.root.findNavController().navigate(toHistoryFragment)
        }

        // pie chart karbohidrat
        pieChartKarbohidrat = binding.pieKarbohidrat

        pieChartKarbohidrat.setUsePercentValues(true)
        pieChartKarbohidrat.description.isEnabled = false
        pieChartKarbohidrat.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChartKarbohidrat.dragDecelerationFrictionCoef = 0.95f

        pieChartKarbohidrat.setDrawHoleEnabled(true)
        pieChartKarbohidrat.setHoleColor(Color.WHITE)

        pieChartKarbohidrat.setTransparentCircleColor(Color.WHITE)
        pieChartKarbohidrat.setTransparentCircleAlpha(110)

        pieChartKarbohidrat.setHoleRadius(58f)
        pieChartKarbohidrat.setTransparentCircleRadius(61f)

        pieChartKarbohidrat.setDrawCenterText(true)

        pieChartKarbohidrat.setRotationAngle(0f)

        pieChartKarbohidrat.setRotationEnabled(true)
        pieChartKarbohidrat.setHighlightPerTapEnabled(true)

        pieChartKarbohidrat.animateY(1400, Easing.EaseInOutQuad)

        pieChartKarbohidrat.legend.isEnabled = false
        pieChartKarbohidrat.setEntryLabelColor(Color.WHITE)
        pieChartKarbohidrat.setEntryLabelTextSize(12f)


        db.collection("users").document(username!!)
            .collection(bulan_makan!!).document(tanggal_makan!!)
            .collection("makan pagi")
            .get().addOnSuccessListener { querySnapshot ->
                var karbohidrat_makan_pagi = 0.0f
                for (document in querySnapshot.documents) {
                    val data = (document.get("karbohidrat").toString() + "F").toFloat()
                    karbohidrat_makan_pagi += data
                }
                db.collection("users").document(username!!)
                    .collection(bulan_makan!!).document(tanggal_makan!!)
                    .collection("makan siang")
                    .get().addOnSuccessListener { querySnapshot ->
                        var karbohidrat_makan_siang = 0.0f
                        for (document in querySnapshot.documents) {
                            val data = (document.get("karbohidrat").toString() + "F").toFloat()
                            karbohidrat_makan_siang += data
                        }
                        db.collection("users").document(username!!)
                            .collection(bulan_makan!!).document(tanggal_makan!!)
                            .collection("makan malam")
                            .get().addOnSuccessListener { querySnapshot ->
                                var karbohidrat_makan_malam = 0.0f
                                for (document in querySnapshot.documents) {
                                    val data = (document.get("karbohidrat").toString() + "F").toFloat()
                                    karbohidrat_makan_malam += data
                                }
                                val entries: ArrayList<PieEntry> = ArrayList()
                                entries.add(PieEntry(karbohidrat_makan_pagi/(karbohidrat_makan_pagi+karbohidrat_makan_siang+karbohidrat_makan_malam)))
                                entries.add(PieEntry(karbohidrat_makan_siang/(karbohidrat_makan_pagi+karbohidrat_makan_siang+karbohidrat_makan_malam)))
                                entries.add(PieEntry(karbohidrat_makan_malam/(karbohidrat_makan_pagi+karbohidrat_makan_siang+karbohidrat_makan_malam)))

                                val dataSet = PieDataSet(entries, "Karbohidrat")
                                dataSet.setDrawIcons(false)

                                binding.tvKarbohidrat.setText("Karbohidrat\n" +
                                        "${String.format("%.2f",karbohidrat_makan_pagi+
                                                karbohidrat_makan_siang+karbohidrat_makan_malam)} gr")

                                // on below line we are setting slice for pie
                                dataSet.sliceSpace = 3f
                                dataSet.iconsOffset = MPPointF(0f, 40f)
                                dataSet.selectionShift = 5f

                                // add a lot of colors to list
                                val colors: ArrayList<Int> = ArrayList()
                                colors.add(resources.getColor(R.color.dark_cyan))
                                colors.add(resources.getColor(R.color.green_apple))
                                colors.add(resources.getColor(R.color.tea_green))

                                // on below line we are setting colors.
                                dataSet.colors = colors

                                // on below line we are setting pie data set
                                val data = PieData(dataSet)
                                data.setValueFormatter(PercentFormatter())
                                data.setValueTextSize(12f)
                                data.setValueTypeface(Typeface.DEFAULT_BOLD)
                                data.setValueTextColor(Color.WHITE)
                                pieChartKarbohidrat.setData(data)

                                // undo all highlights
                                pieChartKarbohidrat.highlightValues(null)

                                // loading chart
                                pieChartKarbohidrat.invalidate()
                            }
                    }
            }

        binding.cvKarbohidrat.setOnClickListener {
            val view = View.inflate(requireContext(), R.layout.alert_history_dialog, null)
            val background = view.findViewById<ConstraintLayout>(R.id.cl_background)
            val tv_title = view.findViewById<TextView>(R.id.tv_title)
            val tv_dikonsumsi = view.findViewById<TextView>(R.id.tv_berat)
            val rv_makan_pagi = view.findViewById<RecyclerView>(R.id.rv_hasil_makan_pagi)
            val rv_makan_siang = view.findViewById<RecyclerView>(R.id.rv_hasil_makan_siang)
            val rv_makan_malam = view.findViewById<RecyclerView>(R.id.rv_hasil_makan_malam)

            tv_title.setText("Karbohidrat")

            db.collection("users").document(username!!)
                .collection(bulan_makan!!).document(tanggal_makan!!)
                .get().addOnSuccessListener {
                    if (it != null){
                        val karbohidrat_dikonsumsi = (it.get("total_konsumsi_karbohidrat").toString() + "F").toFloat()
                        tv_dikonsumsi.setText("${String.format("%.2f", karbohidrat_dikonsumsi)} gr")

                        val status_karbohidrat = it.get("status_konsumsi_karbohidrat").toString()
                        if (status_karbohidrat.equals("Kurang")){
                            background.setBackgroundColor(ContextCompat
                                .getColor(requireContext(), R.color.kurang))
                        } else if (status_karbohidrat.equals("Normal")){
                            background.setBackgroundColor(ContextCompat
                                .getColor(requireContext(), R.color.normal))
                        } else if (status_karbohidrat.equals("Lebih")){
                            background.setBackgroundColor(ContextCompat
                                .getColor(requireContext(), R.color.lebih))
                        }
                    } else {
                        tv_dikonsumsi.setText("0,00 gr")
                        background.setBackgroundColor(ContextCompat
                            .getColor(requireContext(), R.color.white))
                    }
                }

            // makan pagi
            rv_makan_pagi.setHasFixedSize(true)
            rv_makan_pagi.layoutManager = LinearLayoutManager(requireContext())
            rv_makan_pagi.adapter = detailhistorykarbohidratpagiListAdapter
            rv_makan_pagi.itemAnimator = SlideInUpAnimator()

            db.collection("users").document(username!!)
                .collection(bulan_makan!!).document(tanggal_makan!!)
                .collection("makan pagi")
                .orderBy("nama_makanan", Query.Direction.ASCENDING)
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null){
                            Log.e("Firestore Error", error.message.toString())
                        }
                        val doc = arrayListOf<ListDetailHistoryModel>()
                        if(value != null){
                            for (dc in value.documents){
                                val data = dc.toObject(ListDetailHistoryModel::class.java)
                                if (data != null){
                                    doc.add(data)
                                }
                            }
                        }

                        detailhistorykarbohidratpagiListAdapter.detailhistoryList = doc
                        detailhistorykarbohidratpagiListAdapter.notifyDataSetChanged()
                    }
                })

            // makan siang
            rv_makan_siang.setHasFixedSize(true)
            rv_makan_siang.layoutManager = LinearLayoutManager(requireContext())
            rv_makan_siang.adapter = detailhistorykarbohidratsiangListAdapter
            rv_makan_siang.itemAnimator = SlideInUpAnimator()

            db.collection("users").document(username!!)
                .collection(bulan_makan!!).document(tanggal_makan!!)
                .collection("makan siang")
                .orderBy("nama_makanan", Query.Direction.ASCENDING)
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null){
                            Log.e("Firestore Error", error.message.toString())
                        }
                        val doc = arrayListOf<ListDetailHistoryModel>()
                        if(value != null){
                            for (dc in value.documents){
                                val data = dc.toObject(ListDetailHistoryModel::class.java)
                                if (data != null){
                                    doc.add(data)
                                }
                            }
                        }
                        detailhistorykarbohidratsiangListAdapter.detailhistoryList = doc
                        detailhistorykarbohidratsiangListAdapter.notifyDataSetChanged()
                    }
                })

            // makan malam
            rv_makan_malam.setHasFixedSize(true)
            rv_makan_malam.layoutManager = LinearLayoutManager(requireContext())
            rv_makan_malam.adapter = detailhistorykarbohidratmalamListAdapter
            rv_makan_malam.itemAnimator = SlideInUpAnimator()

            db.collection("users").document(username!!)
                .collection(bulan_makan!!).document(tanggal_makan!!)
                .collection("makan malam")
                .orderBy("nama_makanan", Query.Direction.ASCENDING)
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null){
                            Log.e("Firestore Error", error.message.toString())
                        }
                        val doc = arrayListOf<ListDetailHistoryModel>()
                        if(value != null){
                            for (dc in value.documents){
                                val data = dc.toObject(ListDetailHistoryModel::class.java)
                                if (data != null){
                                    doc.add(data)
                                }
                            }
                        }
                        detailhistorykarbohidratmalamListAdapter.detailhistoryList = doc
                        detailhistorykarbohidratmalamListAdapter.notifyDataSetChanged()
                    }
                })

            AlertDialog.Builder(requireContext(), R.style.MyAlertDialogTheme)
                .setView(view)
                .show()

        }

        // pie chart protein
        pieChartProtein = binding.pieProtein

        pieChartProtein.setUsePercentValues(true)
        pieChartProtein.description.isEnabled = false
        pieChartProtein.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChartProtein.dragDecelerationFrictionCoef = 0.95f

        pieChartProtein.setDrawHoleEnabled(true)
        pieChartProtein.setHoleColor(Color.WHITE)

        pieChartProtein.setTransparentCircleColor(Color.WHITE)
        pieChartProtein.setTransparentCircleAlpha(110)

        pieChartProtein.setHoleRadius(58f)
        pieChartProtein.setTransparentCircleRadius(61f)

        pieChartProtein.setDrawCenterText(true)

        pieChartProtein.setRotationAngle(0f)

        pieChartProtein.setRotationEnabled(true)
        pieChartProtein.setHighlightPerTapEnabled(true)

        pieChartProtein.animateY(1400, Easing.EaseInOutQuad)

        pieChartProtein.legend.isEnabled = false
        pieChartProtein.setEntryLabelColor(Color.WHITE)
        pieChartProtein.setEntryLabelTextSize(12f)


        db.collection("users").document(username!!)
            .collection(bulan_makan!!).document(tanggal_makan!!)
            .collection("makan pagi")
            .get().addOnSuccessListener { querySnapshot ->
                var protein_makan_pagi = 0.0f
                for (document in querySnapshot.documents) {
                    val data = (document.get("protein").toString() + "F").toFloat()
                    protein_makan_pagi += data
                }
                db.collection("users").document(username!!)
                    .collection(bulan_makan!!).document(tanggal_makan!!)
                    .collection("makan siang")
                    .get().addOnSuccessListener { querySnapshot ->
                        var protein_makan_siang = 0.0f
                        for (document in querySnapshot.documents) {
                            val data = (document.get("protein").toString() + "F").toFloat()
                            protein_makan_siang += data
                        }
                        db.collection("users").document(username!!)
                            .collection(bulan_makan!!).document(tanggal_makan!!)
                            .collection("makan malam")
                            .get().addOnSuccessListener { querySnapshot ->
                                var protein_makan_malam = 0.0f
                                for (document in querySnapshot.documents) {
                                    val data = (document.get("protein").toString() + "F").toFloat()
                                    protein_makan_malam += data
                                }
                                val entries: ArrayList<PieEntry> = ArrayList()
                                entries.add(PieEntry(protein_makan_pagi/(protein_makan_pagi+protein_makan_siang+protein_makan_malam)))
                                entries.add(PieEntry(protein_makan_siang/(protein_makan_pagi+protein_makan_siang+protein_makan_malam)))
                                entries.add(PieEntry(protein_makan_malam/(protein_makan_pagi+protein_makan_siang+protein_makan_malam)))

                                val dataSet = PieDataSet(entries, "Karbohidrat")
                                dataSet.setDrawIcons(false)

                                binding.tvProtein.setText("Protein\n" +
                                        "${String.format("%.2f",protein_makan_pagi+
                                                protein_makan_siang+protein_makan_malam)} gr")

                                // on below line we are setting slice for pie
                                dataSet.sliceSpace = 3f
                                dataSet.iconsOffset = MPPointF(0f, 40f)
                                dataSet.selectionShift = 5f

                                // add a lot of colors to list
                                val colors: ArrayList<Int> = ArrayList()
                                colors.add(resources.getColor(R.color.dark_cyan))
                                colors.add(resources.getColor(R.color.green_apple))
                                colors.add(resources.getColor(R.color.tea_green))

                                // on below line we are setting colors.
                                dataSet.colors = colors

                                // on below line we are setting pie data set
                                val data = PieData(dataSet)
                                data.setValueFormatter(PercentFormatter())
                                data.setValueTextSize(12f)
                                data.setValueTypeface(Typeface.DEFAULT_BOLD)
                                data.setValueTextColor(Color.WHITE)
                                pieChartProtein.setData(data)

                                // undo all highlights
                                pieChartProtein.highlightValues(null)

                                // loading chart
                                pieChartProtein.invalidate()
                            }
                    }
            }

        //  alert protein
        binding.cvProtein.setOnClickListener {
            val view = View.inflate(requireContext(), R.layout.alert_history_dialog, null)
            val background = view.findViewById<ConstraintLayout>(R.id.cl_background)
            val tv_title = view.findViewById<TextView>(R.id.tv_title)
            val tv_dikonsumsi = view.findViewById<TextView>(R.id.tv_berat)
            val rv_makan_pagi = view.findViewById<RecyclerView>(R.id.rv_hasil_makan_pagi)
            val rv_makan_siang = view.findViewById<RecyclerView>(R.id.rv_hasil_makan_siang)
            val rv_makan_malam = view.findViewById<RecyclerView>(R.id.rv_hasil_makan_malam)

            tv_title.setText("Protein")

            db.collection("users").document(username!!)
                .collection(bulan_makan!!).document(tanggal_makan!!)
                .get().addOnSuccessListener {
                    if (it != null){
                        val protein_dikonsumsi = (it.get("total_konsumsi_protein").toString() + "F").toFloat()
                        tv_dikonsumsi.setText("${String.format("%.2f", protein_dikonsumsi)} gr")

                        val status_protein = it.get("status_konsumsi_protein").toString()
                        if (status_protein.equals("Kurang")){
                            background.setBackgroundColor(ContextCompat
                                .getColor(requireContext(), R.color.kurang))
                        } else if (status_protein.equals("Normal")){
                            background.setBackgroundColor(ContextCompat
                                .getColor(requireContext(), R.color.normal))
                        } else if (status_protein.equals("Lebih")){
                            background.setBackgroundColor(ContextCompat
                                .getColor(requireContext(), R.color.lebih))
                        }
                    } else {
                        tv_dikonsumsi.setText("0,00 gr")
                        background.setBackgroundColor(ContextCompat
                            .getColor(requireContext(), R.color.white))
                    }
                }

            // makan pagi
            rv_makan_pagi.setHasFixedSize(true)
            rv_makan_pagi.layoutManager = LinearLayoutManager(requireContext())
            rv_makan_pagi.adapter = detailhistoryproteinpagiListAdapter
            rv_makan_pagi.itemAnimator = SlideInUpAnimator()

            db.collection("users").document(username!!)
                .collection(bulan_makan!!).document(tanggal_makan!!)
                .collection("makan pagi")
                .orderBy("nama_makanan", Query.Direction.ASCENDING)
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null){
                            Log.e("Firestore Error", error.message.toString())
                        }
                        val doc = arrayListOf<ListDetailHistoryModel>()
                        if(value != null){
                            for (dc in value.documents){
                                val data = dc.toObject(ListDetailHistoryModel::class.java)
                                if (data != null){
                                    doc.add(data)
                                }
                            }
                        }

                        detailhistoryproteinpagiListAdapter.detailhistoryList = doc
                        detailhistoryproteinpagiListAdapter.notifyDataSetChanged()
                    }
                })

            // makan siang
            rv_makan_siang.setHasFixedSize(true)
            rv_makan_siang.layoutManager = LinearLayoutManager(requireContext())
            rv_makan_siang.adapter = detailhistoryproteinsiangListAdapter
            rv_makan_siang.itemAnimator = SlideInUpAnimator()

            db.collection("users").document(username!!)
                .collection(bulan_makan!!).document(tanggal_makan!!)
                .collection("makan siang")
                .orderBy("nama_makanan", Query.Direction.ASCENDING)
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null){
                            Log.e("Firestore Error", error.message.toString())
                        }
                        val doc = arrayListOf<ListDetailHistoryModel>()
                        if(value != null){
                            for (dc in value.documents){
                                val data = dc.toObject(ListDetailHistoryModel::class.java)
                                if (data != null){
                                    doc.add(data)
                                }
                            }
                        }
                        detailhistoryproteinsiangListAdapter.detailhistoryList = doc
                        detailhistoryproteinsiangListAdapter.notifyDataSetChanged()
                    }
                })

            // makan malam
            rv_makan_malam.setHasFixedSize(true)
            rv_makan_malam.layoutManager = LinearLayoutManager(requireContext())
            rv_makan_malam.adapter = detailhistoryproteinmalamListAdapter
            rv_makan_malam.itemAnimator = SlideInUpAnimator()

            db.collection("users").document(username!!)
                .collection(bulan_makan!!).document(tanggal_makan!!)
                .collection("makan malam")
                .orderBy("nama_makanan", Query.Direction.ASCENDING)
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null){
                            Log.e("Firestore Error", error.message.toString())
                        }
                        val doc = arrayListOf<ListDetailHistoryModel>()
                        if(value != null){
                            for (dc in value.documents){
                                val data = dc.toObject(ListDetailHistoryModel::class.java)
                                if (data != null){
                                    doc.add(data)
                                }
                            }
                        }
                        detailhistoryproteinmalamListAdapter.detailhistoryList = doc
                        detailhistoryproteinmalamListAdapter.notifyDataSetChanged()
                    }
                })

            AlertDialog.Builder(requireContext(), R.style.MyAlertDialogTheme)
                .setView(view)
                .show()

        }

        // pie chart lemak
        pieChartLemak = binding.pieLemak

        pieChartLemak.setUsePercentValues(true)
        pieChartLemak.description.isEnabled = false
        pieChartLemak.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChartLemak.dragDecelerationFrictionCoef = 0.95f

        pieChartLemak.setDrawHoleEnabled(true)
        pieChartLemak.setHoleColor(Color.WHITE)

        pieChartLemak.setTransparentCircleColor(Color.WHITE)
        pieChartLemak.setTransparentCircleAlpha(110)

        pieChartLemak.setHoleRadius(58f)
        pieChartLemak.setTransparentCircleRadius(61f)

        pieChartLemak.setDrawCenterText(true)

        pieChartLemak.setRotationAngle(0f)

        pieChartLemak.setRotationEnabled(true)
        pieChartLemak.setHighlightPerTapEnabled(true)

        pieChartLemak.animateY(1400, Easing.EaseInOutQuad)

        pieChartLemak.legend.isEnabled = false
        pieChartLemak.setEntryLabelColor(Color.WHITE)
        pieChartLemak.setEntryLabelTextSize(12f)


        db.collection("users").document(username!!)
            .collection(bulan_makan!!).document(tanggal_makan!!)
            .collection("makan pagi")
            .get().addOnSuccessListener { querySnapshot ->
                var lemak_makan_pagi = 0.0f
                for (document in querySnapshot.documents) {
                    val data = (document.get("lemak").toString() + "F").toFloat()
                    lemak_makan_pagi += data
                }
                db.collection("users").document(username!!)
                    .collection(bulan_makan!!).document(tanggal_makan!!)
                    .collection("makan siang")
                    .get().addOnSuccessListener { querySnapshot ->
                        var lemak_makan_siang = 0.0f
                        for (document in querySnapshot.documents) {
                            val data = (document.get("lemak").toString() + "F").toFloat()
                            lemak_makan_siang += data
                        }
                        db.collection("users").document(username!!)
                            .collection(bulan_makan!!).document(tanggal_makan!!)
                            .collection("makan malam")
                            .get().addOnSuccessListener { querySnapshot ->
                                var lemak_makan_malam = 0.0f
                                for (document in querySnapshot.documents) {
                                    val data = (document.get("lemak").toString() + "F").toFloat()
                                    lemak_makan_malam += data
                                }
                                val entries: ArrayList<PieEntry> = ArrayList()
                                entries.add(PieEntry(lemak_makan_pagi/(lemak_makan_pagi+lemak_makan_siang+lemak_makan_malam)))
                                entries.add(PieEntry(lemak_makan_siang/(lemak_makan_pagi+lemak_makan_siang+lemak_makan_malam)))
                                entries.add(PieEntry(lemak_makan_malam/(lemak_makan_pagi+lemak_makan_siang+lemak_makan_malam)))

                                val dataSet = PieDataSet(entries, "Karbohidrat")
                                dataSet.setDrawIcons(false)

                                binding.tvLemak.setText("Lemak\n" +
                                        "${String.format("%.2f",lemak_makan_pagi+
                                                lemak_makan_siang+lemak_makan_malam)} gr")

                                // on below line we are setting slice for pie
                                dataSet.sliceSpace = 3f
                                dataSet.iconsOffset = MPPointF(0f, 40f)
                                dataSet.selectionShift = 5f

                                // add a lot of colors to list
                                val colors: ArrayList<Int> = ArrayList()
                                colors.add(resources.getColor(R.color.dark_cyan))
                                colors.add(resources.getColor(R.color.green_apple))
                                colors.add(resources.getColor(R.color.tea_green))

                                // on below line we are setting colors.
                                dataSet.colors = colors

                                // on below line we are setting pie data set
                                val data = PieData(dataSet)
                                data.setValueFormatter(PercentFormatter())
                                data.setValueTextSize(12f)
                                data.setValueTypeface(Typeface.DEFAULT_BOLD)
                                data.setValueTextColor(Color.WHITE)
                                pieChartLemak.setData(data)

                                // undo all highlights
                                pieChartLemak.highlightValues(null)

                                // loading chart
                                pieChartLemak.invalidate()
                            }
                    }
            }

        //  alert protein
        binding.cvLemak.setOnClickListener {
            val view = View.inflate(requireContext(), R.layout.alert_history_dialog, null)
            val background = view.findViewById<ConstraintLayout>(R.id.cl_background)
            val tv_title = view.findViewById<TextView>(R.id.tv_title)
            val tv_dikonsumsi = view.findViewById<TextView>(R.id.tv_berat)
            val rv_makan_pagi = view.findViewById<RecyclerView>(R.id.rv_hasil_makan_pagi)
            val rv_makan_siang = view.findViewById<RecyclerView>(R.id.rv_hasil_makan_siang)
            val rv_makan_malam = view.findViewById<RecyclerView>(R.id.rv_hasil_makan_malam)

            tv_title.setText("Lemak")

            db.collection("users").document(username!!)
                .collection(bulan_makan!!).document(tanggal_makan!!)
                .get().addOnSuccessListener {
                    if (it != null){
                        val lemak_dikonsumsi = (it.get("total_konsumsi_lemak").toString() + "F").toFloat()
                        tv_dikonsumsi.setText("${String.format("%.2f", lemak_dikonsumsi)} gr")

                        val status_lemak = it.get("status_konsumsi_lemak").toString()
                        if (status_lemak.equals("Kurang")){
                            background.setBackgroundColor(ContextCompat
                                .getColor(requireContext(), R.color.kurang))
                        } else if (status_lemak.equals("Normal")){
                            background.setBackgroundColor(ContextCompat
                                .getColor(requireContext(), R.color.normal))
                        } else if (status_lemak.equals("Lebih")){
                            background.setBackgroundColor(ContextCompat
                                .getColor(requireContext(), R.color.lebih))
                        }
                    } else {
                        tv_dikonsumsi.setText("0,00 gr")
                        background.setBackgroundColor(ContextCompat
                            .getColor(requireContext(), R.color.white))
                    }
                }

            // makan pagi
            rv_makan_pagi.setHasFixedSize(true)
            rv_makan_pagi.layoutManager = LinearLayoutManager(requireContext())
            rv_makan_pagi.adapter = detailhistorylemakpagiListAdapter
            rv_makan_pagi.itemAnimator = SlideInUpAnimator()

            db.collection("users").document(username!!)
                .collection(bulan_makan!!).document(tanggal_makan!!)
                .collection("makan pagi")
                .orderBy("nama_makanan", Query.Direction.ASCENDING)
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null){
                            Log.e("Firestore Error", error.message.toString())
                        }
                        val doc = arrayListOf<ListDetailHistoryModel>()
                        if(value != null){
                            for (dc in value.documents){
                                val data = dc.toObject(ListDetailHistoryModel::class.java)
                                if (data != null){
                                    doc.add(data)
                                }
                            }
                        }

                        detailhistorylemakpagiListAdapter.detailhistoryList = doc
                        detailhistorylemakpagiListAdapter.notifyDataSetChanged()
                    }
                })

            // makan siang
            rv_makan_siang.setHasFixedSize(true)
            rv_makan_siang.layoutManager = LinearLayoutManager(requireContext())
            rv_makan_siang.adapter = detailhistorylemaksiangListAdapter
            rv_makan_siang.itemAnimator = SlideInUpAnimator()

            db.collection("users").document(username!!)
                .collection(bulan_makan!!).document(tanggal_makan!!)
                .collection("makan siang")
                .orderBy("nama_makanan", Query.Direction.ASCENDING)
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null){
                            Log.e("Firestore Error", error.message.toString())
                        }
                        val doc = arrayListOf<ListDetailHistoryModel>()
                        if(value != null){
                            for (dc in value.documents){
                                val data = dc.toObject(ListDetailHistoryModel::class.java)
                                if (data != null){
                                    doc.add(data)
                                }
                            }
                        }
                        detailhistorylemaksiangListAdapter.detailhistoryList = doc
                        detailhistorylemaksiangListAdapter.notifyDataSetChanged()
                    }
                })

            // makan malam
            rv_makan_malam.setHasFixedSize(true)
            rv_makan_malam.layoutManager = LinearLayoutManager(requireContext())
            rv_makan_malam.adapter = detailhistorylemakmalamListAdapter
            rv_makan_malam.itemAnimator = SlideInUpAnimator()

            db.collection("users").document(username!!)
                .collection(bulan_makan!!).document(tanggal_makan!!)
                .collection("makan malam")
                .orderBy("nama_makanan", Query.Direction.ASCENDING)
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null){
                            Log.e("Firestore Error", error.message.toString())
                        }
                        val doc = arrayListOf<ListDetailHistoryModel>()
                        if(value != null){
                            for (dc in value.documents){
                                val data = dc.toObject(ListDetailHistoryModel::class.java)
                                if (data != null){
                                    doc.add(data)
                                }
                            }
                        }
                        detailhistorylemakmalamListAdapter.detailhistoryList = doc
                        detailhistorylemakmalamListAdapter.notifyDataSetChanged()
                    }
                })

            AlertDialog.Builder(requireContext(), R.style.MyAlertDialogTheme)
                .setView(view)
                .show()

        }

    }
}