import com.dainxt.weaponthrow.capabilities.PlayerThrowData

interface IPlayerEntityMixin {
    fun setThrowPower(value: PlayerThrowData)

    fun getThrowPower(): PlayerThrowData
}