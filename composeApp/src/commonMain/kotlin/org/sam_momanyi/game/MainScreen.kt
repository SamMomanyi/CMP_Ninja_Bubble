package org.sam_momanyi.game

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cmp_ninjabubble.composeapp.generated.resources.Res
import cmp_ninjabubble.composeapp.generated.resources.background
import cmp_ninjabubble.composeapp.generated.resources.run_sprite
import cmp_ninjabubble.composeapp.generated.resources.standing_ninja
import com.stevdza_san.sprite.component.drawSpriteView
import com.stevdza_san.sprite.domain.SpriteFlip
import com.stevdza_san.sprite.domain.SpriteSheet
import com.stevdza_san.sprite.domain.SpriteSpec
import com.stevdza_san.sprite.domain.rememberSpriteState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.sam_momanyi.game.domain.Game
import org.sam_momanyi.game.domain.GameStatus
import org.sam_momanyi.game.domain.MoveDirection
import org.sam_momanyi.game.util.detectMoveGesture


//each ninja frame instance should have the same size
const val NINJA_FRAME_WIDTH = 253
const val NINJA_FRAME_HEIGHT = 303
@Composable
fun MainScreen(modifier : Modifier = Modifier){
    val scope = rememberCoroutineScope()
    var game by remember { mutableStateOf(Game())}
    val weapons = remember { {
    } }
    var moveDirection by remember { mutableStateOf(MoveDirection.None) }
    //to get the screen width and height
    var screenWidth by remember {mutableStateOf(0)}
    var screenHeight by remember {mutableStateOf(0)}

    val runningSprite = rememberSpriteState(
        totalFrames = 9,
        framesPerRow = 3
    )
    //represents a static image
    val standingSprite = rememberSpriteState(
        totalFrames = 1,
        framesPerRow = 1
    )
    val currentRunningFrame by runningSprite.currentFrame.collectAsState()
    val currentStandingFrame by standingSprite.currentFrame.collectAsState()

    val isRunning by runningSprite.isRunning.collectAsState()
    val runningSpriteSpec = remember {
        SpriteSpec(
            screenWidth = screenWidth.toFloat(),
            default = SpriteSheet(
                frameWidth = NINJA_FRAME_WIDTH ,
                frameHeight = NINJA_FRAME_HEIGHT,
                image = Res.drawable.run_sprite
            )
        )
    }
    val standingSpriteSpec = remember {
        SpriteSpec(
            screenWidth = screenWidth.toFloat(),
            default = SpriteSheet(
                frameWidth = NINJA_FRAME_WIDTH ,
                frameHeight = NINJA_FRAME_HEIGHT,
                image = Res.drawable.standing_ninja
            )
        )
    }

    //required parameters for the draw spritesheet
    val runningImage = runningSpriteSpec.imageBitmap
    val standingImage = standingSpriteSpec.imageBitmap

    //we need to pass the screen width so we can calculate any extra screen width if screen width is moved
    val ninjaOffsetX = remember(key1 = screenWidth) {
        Animatable(
            initialValue = ((screenWidth.toFloat()) / 2 - (NINJA_FRAME_WIDTH / 2))
        )
    }

    LaunchedEffect(Unit){
        game = game.copy(
            status = GameStatus.Started
        )
    }
    //box since we stack multiple layers on top of each other
    Box(
        modifier = Modifier
            .fillMaxSize()
            //to get and use the screen height and width()
            .onGloballyPositioned{
                screenWidth = it.size.width
                screenHeight = it.size.height
            }
        // we now specify the pointer event on our composable
            .pointerInput(Unit ){
                awaitPointerEventScope {
                    detectMoveGesture(
                        gameStatus = game.status,
                        onLeft = {
                            moveDirection = MoveDirection.Left
                            runningSprite.start()
                            scope.launch {
                                // Just move it once; the gesture detector handles the repetition
                                val target = (ninjaOffsetX.value - game.settings.ninjaSpeed).coerceAtLeast(0f)
                                ninjaOffsetX.snapTo(target) // snapTo is better for high-frequency updates
                            }
                        },
                        onRight = {
                            moveDirection = MoveDirection.Right
                            runningSprite.start()
                            scope.launch {
                                val target = (ninjaOffsetX.value + game.settings.ninjaSpeed)
                                    .coerceAtMost((screenWidth - NINJA_FRAME_WIDTH).toFloat())
                                ninjaOffsetX.snapTo(target)
                            }
                        },

//                        onLeft = {
//                            moveDirection = MoveDirection.Left
//                            runningSprite.start()
//                            //we need to update and animate the ninjas offset property on when it's running
//                            //we also need to make sure we don't move the ninja outside the screen
//                            scope.launch(Dispatchers.Main) {
//                                while(isRunning){
//                                    ninjaOffsetX.animateTo(
//                                        //we don;t want to allow negative x offset
//                                        targetValue = if((ninjaOffsetX.value - game.settings.ninjaSpeed) >= (NINJA_FRAME_WIDTH/ 2))
//                                        ninjaOffsetX.value - game.settings.ninjaSpeed else ninjaOffsetX.value,
//                                        animationSpec = tween(30)
//                                    )
//                                }
//                            }
//                        },
//                        onRight = {
//                            moveDirection = MoveDirection.Right
//                            runningSprite.start()
//                            scope.launch(Dispatchers.Main) {
//                                while(isRunning){
//                                    ninjaOffsetX.animateTo(
//                                        //we don;t want to allow negative x offset
//                                        targetValue = if((ninjaOffsetX.value - game.settings.ninjaSpeed + NINJA_FRAME_WIDTH) <= screenWidth + (NINJA_FRAME_WIDTH/ 2))
//                                            ninjaOffsetX.value + game.settings.ninjaSpeed else ninjaOffsetX.value,
//                                        animationSpec = tween(30)
//                                    )
//                                }
//                            }
//                        },
                        onFingerLifted = {
                            moveDirection = MoveDirection.None
                            runningSprite.stop()
                        }
                    )
                }
            }
    ){
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(Res.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        //here we draw the ninja sprite sheets animations as well as target
        Canvas(modifier = Modifier.fillMaxSize()){
            //we will use both spritesheet files
            drawSpriteView(
                spriteState = if(isRunning) runningSprite else standingSprite,
                spriteSpec = if(isRunning) runningSpriteSpec else standingSpriteSpec,
                currentFrame = if(isRunning) currentRunningFrame else currentStandingFrame,
                image = if(isRunning) runningImage else standingImage,
                spriteFlip = if(moveDirection == MoveDirection.Left)
                    SpriteFlip.Horizontal else null,
                //we need to calculate the movement of the ninja when we use the  finger gestures
                offset = IntOffset(
                    x = ninjaOffsetX.value.toInt(),
                    //we place the ninja at the bottom of the screen
                    y = (screenHeight - NINJA_FRAME_HEIGHT - (NINJA_FRAME_HEIGHT / 2))
                )
            )
        }
    }
}