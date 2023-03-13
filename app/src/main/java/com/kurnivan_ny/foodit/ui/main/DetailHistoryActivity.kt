package com.kurnivan_ny.foodit.ui.main

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.firestore.FirebaseFirestore
import com.kurnivan_ny.foodit.R
import com.kurnivan_ny.foodit.databinding.ActivityDetailHistoryBinding
import com.kurnivan_ny.foodit.ui.main.fragment.history.HistoryFragment
import com.kurnivan_ny.foodit.viewmodel.preferences.SharedPreferences

class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryBinding

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var db: FirebaseFirestore

    private lateinit var pieChartKarbohidrat: PieChart
    private lateinit var pieChartProtein: PieChart
    private lateinit var pieChartLemak: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = SharedPreferences(this)
        db = FirebaseFirestore.getInstance()

        val username = sharedPreferences.getValuesString("username")
        val tanggal_makan = intent.getStringExtra("tanggal_makan").toString()
        val bulan_makan = intent.getStringExtra("bulan_makan").toString()

        binding.ivBack.setOnClickListener {
            val intent = Intent(this@DetailHistoryActivity, HomeActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        binding.ivDelete.setOnClickListener {

            // delete data
            db.collection("users").document(username!!)
                .collection(bulan_makan).document(tanggal_makan)
                .delete()

            val intent = Intent(this@DetailHistoryActivity, HomeActivity::class.java)
            startActivity(intent)
            finishAffinity()
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
            .collection(bulan_makan).document(tanggal_makan)
            .collection("makan pagi")
            .get().addOnSuccessListener { querySnapshot ->
                var karbohidrat_makan_pagi = 0.0f
                for (document in querySnapshot.documents) {
                    val data = (document.get("karbohidrat").toString() + "F").toFloat()
                    karbohidrat_makan_pagi += data
                }
                db.collection("users").document(username!!)
                    .collection(bulan_makan).document(tanggal_makan)
                    .collection("makan siang")
                    .get().addOnSuccessListener { querySnapshot ->
                        var karbohidrat_makan_siang = 0.0f
                        for (document in querySnapshot.documents) {
                            val data = (document.get("karbohidrat").toString() + "F").toFloat()
                            karbohidrat_makan_siang += data
                        }
                        db.collection("users").document(username!!)
                            .collection(bulan_makan).document(tanggal_makan)
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
            .collection(bulan_makan).document(tanggal_makan)
            .collection("makan pagi")
            .get().addOnSuccessListener { querySnapshot ->
                var protein_makan_pagi = 0.0f
                for (document in querySnapshot.documents) {
                    val data = (document.get("protein").toString() + "F").toFloat()
                    protein_makan_pagi += data
                }
                db.collection("users").document(username!!)
                    .collection(bulan_makan).document(tanggal_makan)
                    .collection("makan siang")
                    .get().addOnSuccessListener { querySnapshot ->
                        var protein_makan_siang = 0.0f
                        for (document in querySnapshot.documents) {
                            val data = (document.get("protein").toString() + "F").toFloat()
                            protein_makan_siang += data
                        }
                        db.collection("users").document(username!!)
                            .collection(bulan_makan).document(tanggal_makan)
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
            .collection(bulan_makan).document(tanggal_makan)
            .collection("makan pagi")
            .get().addOnSuccessListener { querySnapshot ->
                var lemak_makan_pagi = 0.0f
                for (document in querySnapshot.documents) {
                    val data = (document.get("lemak").toString() + "F").toFloat()
                    lemak_makan_pagi += data
                }
                db.collection("users").document(username!!)
                    .collection(bulan_makan).document(tanggal_makan)
                    .collection("makan siang")
                    .get().addOnSuccessListener { querySnapshot ->
                        var lemak_makan_siang = 0.0f
                        for (document in querySnapshot.documents) {
                            val data = (document.get("lemak").toString() + "F").toFloat()
                            lemak_makan_siang += data
                        }
                        db.collection("users").document(username!!)
                            .collection(bulan_makan).document(tanggal_makan)
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

    }
}