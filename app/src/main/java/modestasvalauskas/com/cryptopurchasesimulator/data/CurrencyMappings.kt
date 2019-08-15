package modestasvalauskas.com.cryptopurchasesimulator.data

import modestasvalauskas.com.cryptopurchasesimulator.R


enum class CurrencyMappings(var naming: String, var shortnaming: String, var resID: Int) {
    BITCOIN                 ("Bitcoin", "BTC", R.drawable.bitcoin),
    ETHEREUM                ("Ethereum", "ETH", R.drawable.ethereum),
    BITCOINCASH             ("Bitcoin Cash", "BCH", R.drawable.bitcoincash),
    BITCOINGOLD             ("Bitcoin Gold", "BTG", R.drawable.bitcoingold),
    RIPPLE                  ("Ripple", "XRP", R.drawable.ripple),
    LITECOIN                ("Litecoin", "LTC", R.drawable.litecoin),
    NEM                     ("NEM", "XEM", R.drawable.nem),
    DASH                    ("Dash", "DASH", R.drawable.dash),
    IOTA                    ("Iota", "MIOTA", R.drawable.iota),
    MONERO                  ("Monero", "XMR", R.drawable.monero),
    NEO                     ("NEO", "NEO", R.drawable.neo),
    ETHEREUMCLASSIC         ("Ethereum Classic", "ETC", R.drawable.ethereumclassic),
    OMISEGO                 ("OmiseGO", "OMG", R.drawable.omisego),
    HSHARE                  ("Hshare", "HSR", R.drawable.hshare),
    QTUM                    ("Qtum", "QTUM", R.drawable.qtum),
    BITCONNECT              ("BitConnect", "BCC", R.drawable.bitconnect),
    LISK                    ("Lisk", "LSK", R.drawable.lisk),
    STRATIS                 ("Stratis", "STRAT", R.drawable.stratis),
    WAVES                   ("Waves", "WAVES", R.drawable.waves),
    ZCASH                   ("Zcash", "ZEC", R.drawable.zcash),
    BYTECOIN                ("Bytecoin", "BCN", R.drawable.bytecoinbcn),
    TENX                    ("TenX", "PAY", R.drawable.tenx),
    EOS                     ("EOS", "EOS", R.drawable.eos),
    BITSHARES               ("BitShares", "BTS", R.drawable.bitshares),
    STEEM                   ("Steem", "STEEM", R.drawable.steem),
    STELLARLUMENS           ("Stellar Lumens", "XLM", R.drawable.stellar),
    TETHER                  ("Tether", "USDT", R.drawable.tether),
    MAIDSAFECOIN            ("MaidSafeCoin", "MAID", R.drawable.maidsafecoin),
    AUGUR                   ("Augur", "REP", R.drawable.augur),
    GOLEM                   ("Golem", "GNT", R.drawable.golemnetworktokens),
    BASICATTENTIONTOKEN     ("Basic Attention Token", "BAT", R.drawable.basicattentiontoken),
    FACTOM                  ("Factom", "FCT", R.drawable.factom),
    ICONOMI                 ("Iconomi", "ICN", R.drawable.iconomi),
    SIACOIN                 ("Siacoin", "SC", R.drawable.siacoin),
    BYTEBALL                ("Byteball", "GBYTE", R.drawable.byteball),
    DOGECOIN                ("Dogecoin", "DOGE", R.drawable.dogecoin),
    ARK                     ("Ark", "ARK", R.drawable.ark),
    BINANCECOIN             ("Binance Coin", "BNB", R.drawable.binancecoin),
    DECRED                  ("Decred", "DCR", R.drawable.decred),
    DIGIXDAO                ("DigixDAO", "DGD", R.drawable.digixdao),
    GNOSIS                  ("Gnosis", "GNO", R.drawable.gnosisgno),
    LBRYCREDITS             ("LBRY Credits", "LBC", R.drawable.librarycredit),
    VERITASEUM              ("Veritaseum", "VERI", R.drawable.veritaseum),
    METAL                   ("Metal", "MTL", R.drawable.metal),
    NEXUS                   ("Nexus", "NXS", R.drawable.nexus),
    CIVIC                   ("Civic", "CVC", R.drawable.civic),
    ZEROX                   ("0x", "ZRX", R.drawable.coin0x),
    PIVX                    ("PIVX", "PIVX", R.drawable.pivx),
    STATUS                  ("Status", "SNT", R.drawable.status),
    DIGIBYTE                ("DigiByte", "DGB", R.drawable.digibyte),
    KOMODO                  ("Komodo", "KMD", R.drawable.komodo),
    POPULOUS                ("Populous", "PPT", R.drawable.populous),
    CARDANO                 ("Cardano", "ADA", R.drawable.cardano),
    GAS                     ("Gas", "GAS", R.drawable.gas),
    GAMECREDITS             ("GameCredits", "GAME", R.drawable.gamecredits),
    ARDOR                   ("Ardor", "ARDR", R.drawable.ardor),
    GXSHARES                ("GXShares", "GXS", R.drawable.gxshares),
    BLOCKNET                ("Blocknet", "BLOCK", R.drawable.blocknet),
    FUNFAIR                 ("FunFair", "FUN", R.drawable.funfair),
    MCAP                    ("MCAP", "MCAP", R.drawable.mcap),
    BANCOR                  ("Bancor", "BNT", R.drawable.bancor),
    MONACO                  ("Monaco", "MCO", R.drawable.monaco),
    BYTOM                   ("Bytom", "BTM", R.drawable.bytom),
    NXT                     ("Nxt", "NXT", R.drawable.nxt),
    UBIQ                    ("Ubiq", "UBQ", R.drawable.ubiq),
    SYSCOIN                 ("Syscoin", "SYS", R.drawable.syscoin),
    SINGULARDTV             ("SingularDTV", "SNGLS", R.drawable.singulardtv),
    ARAGON                  ("Aragon", "ANT", R.drawable.aragon),
    STORJ                   ("Storj", "STORJ", R.drawable.storj),
    LYKKE                   ("Lykke", "LKK", R.drawable.lykke),
    NOLIMITCOIN             ("NoLimitCoin", "LNC2", R.drawable.nolimitcoin),
    VERGE                   ("Verge", "XVG", R.drawable.verge),
    BITCOINDARK             ("BitcoinDark", "BTCD", R.drawable.bitcoindark),
    MOBILEGO                ("MobileGo", "MGO", R.drawable.mobilego),
    PARTICL                 ("Marticl", "PART", R.drawable.particl),
    TIERION                 ("Tiorion", "TNT", R.drawable.tierion),
    COFOUNDDOTIT            ("Cofound.it", "CFI", R.drawable.cofoundit),
    METAVERSEETP            ("Metaverse ETP", "ETP", R.drawable.metaverse),
    WINGS                   ("Wings", "WINGS", R.drawable.wings),
//    ICO                     ("ICO", "ICO", R.drawable.ico),
    EDGELESS                ("Edgeless", "EDG", R.drawable.edgeless),
    DISTRICTZEROX           ("district0x", "DNT", R.drawable.district0x),
//    BITQUENCE               ("Bisequence", "BQX", R.drawable.bitquence),
    ASCH                    ("Asch", "XAS", R.drawable.asch),
    DECENT                  ("DECENT", "DCT", R.drawable.decent),
    NAVCOIN                 ("NAV Coin", "NAC", R.drawable.navcoin),
    ADEX                    ("AdEx", "ADX", R.drawable.adxnet),
    COINDASH                ("CoinDash", "CDT", R.drawable.coindash),
    PILLAR                  ("Pillar", "PLR", R.drawable.pillar),
    FIRSTBLOOD              ("FirstBlood", "1ST", R.drawable.firstblood),
    EMERCOIN                ("Emercoin", "EMR", R.drawable.emercoin),
    IEXECRLC                ("iExec RLC", "RLC", R.drawable.rlc),
    PEERCOIN                ("Peercoin", "PPC", R.drawable.peercoin),
    RISE                    ("Rise", "RISE", R.drawable.rise),
    TOKENCARD               ("TokenCard", "TKN", R.drawable.tokencard),
    MELON                   ("Melon", "MLN", R.drawable.melon),
    REDDCOIN                ("ReddCoin", "RDD", R.drawable.reddcoin),
    LEOCOIN                 ("LEOcoin", "LEO", R.drawable.leocoin),
    FIRSTCOIN               ("FirstCoin", "FRST", R.drawable.firstcoin),
    GULDEN                  ("Gulden", "NLG", R.drawable.gulden),
    NUMERAIRE               ("Numeraire", "NMR", R.drawable.numeraire),
    ISLASHOCOIN             ("I/O Coin", "IOC", R.drawable.iocoin),
    ELASTIC                 ("Elastic", "XEL", R.drawable.elastic),
    TRON                    ("TRON", "TRX", R.drawable.tron),
    MONACOIN                ("MonaCoin", "MONA", R.drawable.monacoin),
    SALT                    ("SALT", "SALT", R.drawable.salt),
    EINSTEINIUM             ("Einsteinium", "EMC2", R.drawable.einsteinium),
    VERTCOIN                ("Vertcoin", "VTC", R.drawable.vertcoin),

}


class CurrencyMappingsToCoinMarketCapID {

    companion object {
        var map: Map<CurrencyMappings, String> = mapOf(
                Pair(CurrencyMappings.BITCOIN                 , "bitcoin"),
                Pair(CurrencyMappings.ETHEREUM                , "ethereum"),
                Pair(CurrencyMappings.BITCOINCASH             , "bitcoin-cash"),
                Pair(CurrencyMappings.BITCOINGOLD             , "bitcoin-gold"),
                Pair(CurrencyMappings.RIPPLE                  , "ripple"),
                Pair(CurrencyMappings.LITECOIN                , "litecoin"),
                Pair(CurrencyMappings.NEM                     , "nem"),
                Pair(CurrencyMappings.DASH                    , "dash"),
                Pair(CurrencyMappings.IOTA                    , "iota"),
                Pair(CurrencyMappings.MONERO                  , "monero"),
                Pair(CurrencyMappings.NEO                     , "neo"),
                Pair(CurrencyMappings.ETHEREUMCLASSIC         , "ethereum-classic"),
                Pair(CurrencyMappings.OMISEGO                 , "omisego"),
                Pair(CurrencyMappings.HSHARE                  , "hshare"),
                Pair(CurrencyMappings.QTUM                    , "qtum"),
                Pair(CurrencyMappings.BITCONNECT              , "bitconnect"),
                Pair(CurrencyMappings.LISK                    , "lisk"),
                Pair(CurrencyMappings.STRATIS                 , "stratis"),
                Pair(CurrencyMappings.WAVES                   , "waves"),
                Pair(CurrencyMappings.ZCASH                   , "zcash"),
                Pair(CurrencyMappings.BYTECOIN                , "bytecoin-bcn"),
                Pair(CurrencyMappings.TENX                    , "tenx"),
                Pair(CurrencyMappings.EOS                     , "eos"),
                Pair(CurrencyMappings.BITSHARES               , "bitshares"),
                Pair(CurrencyMappings.STEEM                   , "steem"),
                Pair(CurrencyMappings.STELLARLUMENS           , "stellar"),
                Pair(CurrencyMappings.TETHER                  , "tether"),
                Pair(CurrencyMappings.MAIDSAFECOIN            , "maidsafecoin"),
                Pair(CurrencyMappings.AUGUR                   , "augur"),
                Pair(CurrencyMappings.GOLEM                   , "golem-network-tokens"),
                Pair(CurrencyMappings.BASICATTENTIONTOKEN     , "basic-attention-token"),
                Pair(CurrencyMappings.FACTOM                  , "factom"),
                Pair(CurrencyMappings.ICONOMI                 , "iconomi"),
                Pair(CurrencyMappings.SIACOIN                 , "siacoin"),
                Pair(CurrencyMappings.BYTEBALL                , "byteball"),
                Pair(CurrencyMappings.DOGECOIN                , "dogecoin"),
                Pair(CurrencyMappings.ARK                     , "ark"),
                Pair(CurrencyMappings.BINANCECOIN             , "binance-coin"),
                Pair(CurrencyMappings.DECRED                  , "decred"),
                Pair(CurrencyMappings.DIGIXDAO                , "digixdao"),
                Pair(CurrencyMappings.GNOSIS                  , "gnosis-gno"),
                Pair(CurrencyMappings.LBRYCREDITS             , "library-credit"),
                Pair(CurrencyMappings.VERITASEUM              , "veritaseum"),
                Pair(CurrencyMappings.METAL                   , "metal"),
                Pair(CurrencyMappings.NEXUS                   , "nexus"),
                Pair(CurrencyMappings.CIVIC                   , "civic"),
                Pair(CurrencyMappings.ZEROX                   , "0x"),
                Pair(CurrencyMappings.PIVX                    , "pivx"),
                Pair(CurrencyMappings.STATUS                  , "status"),
                Pair(CurrencyMappings.DIGIBYTE                , "digibyte"),
                Pair(CurrencyMappings.KOMODO                  , "komodo"),
                Pair(CurrencyMappings.POPULOUS                , "populous"),
                Pair(CurrencyMappings.CARDANO                 , "cardano"),
                Pair(CurrencyMappings.GAS                     , "gas"),
                Pair(CurrencyMappings.GAMECREDITS             , "gamecredits"),
                Pair(CurrencyMappings.ARDOR                   , "ardor"),
                Pair(CurrencyMappings.GXSHARES                , "gxshares"),
                Pair(CurrencyMappings.BLOCKNET                , "blocknet"),
                Pair(CurrencyMappings.FUNFAIR                 , "funfair"),
                Pair(CurrencyMappings.MCAP                    , "mcap"),
                Pair(CurrencyMappings.BANCOR                  , "bancor"),
                Pair(CurrencyMappings.MONACO                  , "monaco"),
                Pair(CurrencyMappings.BYTOM                   , "bytom"),
                Pair(CurrencyMappings.NXT                     , "nxt"),
                Pair(CurrencyMappings.UBIQ                    , "ubiq"),
                Pair(CurrencyMappings.SYSCOIN                 , "syscoin"),
                Pair(CurrencyMappings.SINGULARDTV             , "singulardtv"),
                Pair(CurrencyMappings.ARAGON                  , "aragon"),
                Pair(CurrencyMappings.STORJ                   , "storj"),
                Pair(CurrencyMappings.LYKKE                   , "lykke"),
                Pair(CurrencyMappings.NOLIMITCOIN             , "nolimitcoin"),
                Pair(CurrencyMappings.VERGE                   , "verge"),
                Pair(CurrencyMappings.BITCOINDARK             , "bitcoindark"),
                Pair(CurrencyMappings.MOBILEGO                , "mobilego"),
                Pair(CurrencyMappings.PARTICL                 , "particl"),
                Pair(CurrencyMappings.TIERION                 , "tierion"),
                Pair(CurrencyMappings.COFOUNDDOTIT            , "cofound-it"),
                Pair(CurrencyMappings.METAVERSEETP            , "metaverse"),
                Pair(CurrencyMappings.WINGS                   , "wings"),
                //Pair(CurrencyMappings.ICO                     , "ico"),
                Pair(CurrencyMappings.EDGELESS                , "edgeless"),
                Pair(CurrencyMappings.DISTRICTZEROX           , "district0x"),
//                Pair(CurrencyMappings.BITQUENCE               , "bitquence"),
                Pair(CurrencyMappings.ASCH                    , "asch"),
                Pair(CurrencyMappings.DECENT                  , "decent"),
                Pair(CurrencyMappings.NAVCOIN                 , "nav-coin"),
                Pair(CurrencyMappings.ADEX                    , "adx-net"),
                Pair(CurrencyMappings.COINDASH                , "coindash"),
                Pair(CurrencyMappings.PILLAR                  , "pillar"),
                Pair(CurrencyMappings.FIRSTBLOOD              , "firstblood"),
                Pair(CurrencyMappings.EMERCOIN                , "emercoin"),
                Pair(CurrencyMappings.IEXECRLC                , "rlc"),
                Pair(CurrencyMappings.PEERCOIN                , "peercoin"),
                Pair(CurrencyMappings.RISE                    , "rise"),
                Pair(CurrencyMappings.TOKENCARD               , "tokencard"),
                Pair(CurrencyMappings.MELON                   , "melon"),
                Pair(CurrencyMappings.REDDCOIN                , "reddcoin"),
                Pair(CurrencyMappings.LEOCOIN                 , "leocoin"),
                Pair(CurrencyMappings.FIRSTCOIN               , "firstcoin"),
                Pair(CurrencyMappings.GULDEN                  , "gulden"),
                Pair(CurrencyMappings.NUMERAIRE               , "numeraire"),
                Pair(CurrencyMappings.ISLASHOCOIN             , "iocoin"),
                Pair(CurrencyMappings.ELASTIC                 , "elastic"),
                Pair(CurrencyMappings.TRON                    , "tron"),
                Pair(CurrencyMappings.MONACOIN                , "monacoin"),
                Pair(CurrencyMappings.SALT                    , "salt"),
                Pair(CurrencyMappings.EINSTEINIUM             , "einsteinium"),
                Pair(CurrencyMappings.VERTCOIN                , "vertcoin")
        )

    }


}