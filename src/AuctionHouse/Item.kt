package AuctionHouse

import java.io.Serializable

data class Item(
    @JvmField val description: String,
    @JvmField var auctionID: Int,
) : Serializable {
    @JvmField var currentBid = 0

    override fun toString() = "$description. AuctionID: $auctionID current bid $currentBid"
}
