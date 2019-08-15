package modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.NothingToSellFragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.romainpiel.shimmer.Shimmer
import kotlinx.android.synthetic.main.fragment_nothing_to_sell.*
import modestasvalauskas.com.cryptopurchasesimulator.R
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.TitleInterface


class NothingToSellFragment : Fragment() {

    lateinit var titleInterface: TitleInterface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nothing_to_sell, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        titleInterface = (activity as TitleInterface)
    }

    override fun onStart() {
        super.onStart()
        titleInterface.setTitle(activity!!.resources.getString(R.string.sell))
        titleInterface.setSubtitle("")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var shimmer = Shimmer()
        shimmer
                .setDuration(3000)
                .setStartDelay(1000)
                .setDirection(Shimmer.ANIMATION_DIRECTION_LTR)
        shimmer.start(shimmer_tv)
    }

}
