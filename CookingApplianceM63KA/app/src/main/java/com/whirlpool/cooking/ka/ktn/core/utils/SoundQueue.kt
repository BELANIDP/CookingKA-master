package core.utils

/**
 * File        : com.whirlpool.cooking.utils.SoundQueue <br></br>
 * Brief       : SoundQueue class having audio detail like audioId and sound type <br></br>
 * so audio id may be any raw folder resource file <br></br>
 * where sound type either STREAM_ALARM or STREAM_SYSTEM <br></br>
 * Author      : GOYALM5 <br></br>
 * Created On  : 02-05-2024 <br></br>
 * Details     : This Util class is to having all the Audio Related details. This is the only <br></br>
 * class which is used for queueing purpose. so we queue and poll all sound one by one if it comes <br></br>
 * when one file is playing.<br></br>
 */
class SoundQueue(var uId: Long, var audioId: Int, var tsToPlay: Long, var soundType: Int)