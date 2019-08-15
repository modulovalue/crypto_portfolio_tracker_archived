package modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.statsfragment

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_stats.*
import modestasvalauskas.com.cryptopurchasesimulator.R
import modestasvalauskas.com.cryptopurchasesimulator.persistence.TransactionData
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.MainActivity
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.PriceDataInterface
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Refreshable
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.TitleInterface
import modestasvalauskas.com.cryptopurchasesimulator.utils.GlobalSettings
import modestasvalauskas.com.cryptopurchasesimulator.utils.SortBy
import modestasvalauskas.com.cryptopurchasesimulator.utils.TextFormatter
import java.util.*
import kotlin.collections.ArrayList


class StatsFragment : Fragment(), Observer {

    val priceDataInterface: PriceDataInterface by lazy { (activity as PriceDataInterface) }

    val titleInterface: TitleInterface by lazy { (activity as TitleInterface) }

    var filterString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).observableNewData.addObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).observableNewData.deleteObserver(this)
    }

    override fun update(p0: Observable?, t: Any?) {
        if (statsrecyclerview != null) {

            val list = ArrayList(priceDataInterface
                    .getData()
                    .map {
                        Pair(it, TransactionData.isCurrencyMarked(it.currencyMappings))
                    }.filter {
                if (filterString != "") {
                    it.first.currencyMappings.naming.startsWith(filterString, true)
                } else {
                    true
                }
            })

            val marked = GlobalSettings.sortEnum.sort(ArrayList(list.filter { it.second }))
            val notMarked = GlobalSettings.sortEnum.sort(ArrayList(list.filter { !it.second }))
            val concat = marked + notMarked

            (statsrecyclerview.adapter as MyAdapter).arrayList = ArrayList(concat)
            (statsrecyclerview.adapter as MyAdapter).notifyDataSetChanged()
            titleInterface.setSubtitle(activity!!.resources.getString(R.string.updatedat) + " " + TextFormatter.timestampToLastRefreshed(GlobalSettings.lastRefreshed, context!!))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onStart() {
        super.onStart()
        titleInterface.setTitle(activity!!.resources.getString(R.string.statstitle))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statsrecyclerview.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        statsrecyclerview.adapter = MyAdapter({ activity!! }, { (activity as MainActivity).observableNewData.notifyObservers() }, arrayListOf(), context!!, R.layout.itemstatscell)
        statsrecyclerview.addItemDecoration(DividerItemDecoration(context, (statsrecyclerview.layoutManager as LinearLayoutManager).orientation))

        stats_refresh_layout.setColorSchemeResources(R.color.whitecolor)
        stats_refresh_layout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
        stats_refresh_layout.setOnRefreshListener {
            (activity as Refreshable).refresh()
            stats_refresh_layout.isRefreshing = false
        }

        statsfiltertextbox.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val d = p0.toString()
                filterString = d
                update(null, null)
            }
        })

        val closeKeyboard = {
            val inputMethodManager = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }
        statsfiltertextbox.setOnKeyListener { _, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                closeKeyboard()
                true
            } else {
                false
            }
        }

        var firstTime = true
        val aa = ArrayAdapter(
                context,
                R.layout.sortlayoutspinnerstyle,
                SortBy.values().map { resources.getString(it.stringID) })
        aa.setDropDownViewResource(R.layout.sortlayoutspinnerstyle)

        statsSortSpinner.adapter = aa
        statsSortSpinner.setSelection(SortBy.values().indexOf(GlobalSettings.sortEnum))
        statsSortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.e("LOG", " $    $    $    $    ")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                GlobalSettings.sortEnum = SortBy.values()[position]
                if (!firstTime) {
                    update(null, null)
                }
                firstTime = false
            }
        }

        statsrecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                closeKeyboard()
            }
        })
        update(null, null)

    }

}
