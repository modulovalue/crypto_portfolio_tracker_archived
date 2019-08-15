package modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.BuyFragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_buy.*
import modestasvalauskas.com.cryptopurchasesimulator.R
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.MainActivity
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.PriceDataInterface
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.TitleInterface
import java.util.*


class BuyFragment : Fragment() {

    lateinit var priceDataInterface: PriceDataInterface
    lateinit var titleInterface: TitleInterface

    var newDataObservable = Observer { _, _ ->
        if (buyrecyclerview != null) {
            (buyrecyclerview.adapter as MyAdapter).arrayList = priceDataInterface.getData()
            (buyrecyclerview.adapter as MyAdapter).notifyDataSetChanged()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_buy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buyrecyclerview.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        buyrecyclerview.adapter = MyAdapter({ activity!! }, arrayListOf(), context!!, R.layout.itembuycell)
        buyrecyclerview.addItemDecoration(DividerItemDecoration(context, (buyrecyclerview.layoutManager as LinearLayoutManager).orientation))
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

    override fun onStart() {
        super.onStart()
        titleInterface.setTitle(activity!!.resources.getString(R.string.buy))
        titleInterface.setSubtitle("")
        newDataObservable.update(null, null)
    }

}