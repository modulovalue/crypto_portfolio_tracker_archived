package modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.SellFragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_sell.*
import modestasvalauskas.com.cryptopurchasesimulator.R
import modestasvalauskas.com.cryptopurchasesimulator.persistence.TransactionData
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.MainActivity
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.PriceDataInterface
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.TitleInterface
import java.util.Observer
import kotlin.collections.ArrayList


class SellFragment : Fragment() {

    private lateinit var priceDataInterface: PriceDataInterface

    private lateinit var titleInterface: TitleInterface

    private var newDataObservable = Observer { _, _ ->
        if (sellrecyclerview != null) {
            (sellrecyclerview.adapter as MyAdapter).arrayList = ArrayList(priceDataInterface.getData()
                    .filter {
                        TransactionData.hasTransacted(it.currencyMappings)
                    })
            (sellrecyclerview.adapter as MyAdapter).notifyDataSetChanged()
        }
        updateCurrentPortfolioValue()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sell, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sellrecyclerview.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        sellrecyclerview.adapter = MyAdapter({ activity!! }, { newDataObservable.update(null, null) }, arrayListOf(), context!!, R.layout.itemsellcell)
        sellrecyclerview.addItemDecoration(DividerItemDecoration(context, (sellrecyclerview.layoutManager as LinearLayoutManager).orientation))
    }

    override fun onStart() {
        super.onStart()
        titleInterface.setTitle(activity!!.resources.getString(R.string.sell))
        updateCurrentPortfolioValue()
        newDataObservable.update(null, null)
    }

    private fun updateCurrentPortfolioValue() {
        titleInterface.setSubtitle(TransactionData.currentPortfolioValueString(priceDataInterface.getData()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        priceDataInterface = (activity as PriceDataInterface)
        titleInterface = (activity as TitleInterface)
        (activity as MainActivity).observableNewData.addObserver(newDataObservable)
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).observableNewData.deleteObserver(newDataObservable)
    }

}

