package modestasvalauskas.com.cryptopurchasesimulator.utils

import java.util.*


class WorkingObservable : Observable() {

    override fun notifyObservers() {
        setChanged()
        super.notifyObservers()
    }

}