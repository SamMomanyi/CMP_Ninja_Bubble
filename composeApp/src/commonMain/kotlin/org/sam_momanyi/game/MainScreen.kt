package org.sam_momanyi.game

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cmp_ninjabubble.composeapp.generated.resources.Res
import cmp_ninjabubble.composeapp.generated.resources.background
import cmp_ninjabubble.composeapp.generated.resources.kunai
import cmp_ninjabubble.composeapp.generated.resources.run_sprite
import cmp_ninjabubble.composeapp.generated.resources.standing_ninja
import com.stevdza_san.sprite.component.drawSpriteView
import com.stevdza_san.sprite.domain.SpriteFlip
import com.stevdza_san.sprite.domain.SpriteSheet
import com.stevdza_san.sprite.domain.SpriteSpec
import com.stevdza_san.sprite.domain.rememberSpriteState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.sam_momanyi.game.domain.Game
import org.sam_momanyi.game.domain.GameStatus
import org.sam_momanyi.game.domain.MoveDirection
import org.sam_momanyi.game.domain.Weapon
import org.sam_momanyi.game.domain.target.EasyTarget
import org.sam_momanyi.game.domain.target.MediumTarget
import org.sam_momanyi.game.domain.target.StrongTarget
import org.sam_momanyi.game.domain.target.Target
import org.sam_momanyi.game.util.detectMoveGesture


//each ninja frame instance should have the same size
const val NINJA_FRAME_WIDTH = 253
const val NINJA_FRAME_HEIGHT = 303
//we keep spawning weapons every 150L
const val WEAPON_SPAWN_RATE = 150L
const val WEAPON_SIZE = 32f
const val TARGET_SPAWN_RATE  = 1500L
const val TARGET_SIZE = 40f

@Composable
fun MainScreen(modifier : Modifier = Modifier){
    val scope = rememberCoroutineScope()
    var game by remember { mutableStateOf(Game())}
    //create a mutable list variable for arrows
    val weapons = remember {
        mutableStateListOf<Weapon>()
    }
    val targets = remember { mutableStateListOf<Target>() }
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
    val kunaiImage = imageResource(Res.drawable.kunai)

    //we need to pass the screen width so we can calculate any extra screen width if screen width is moved
    val ninjaOffsetX = remember(key1 = screenWidth) {
        Animatable(
            initialValue = ((screenWidth.toFloat()) / 2 - (NINJA_FRAME_WIDTH / 2))
        )
    }



    //when ninja is running and when game status is changed
    LaunchedEffect(isRunning,game.status){
        while(isRunning && game.status == GameStatus.Started){
            delay(WEAPON_SPAWN_RATE)
            weapons.add(
                Weapon(
                    //center of ninja frame a little above it
                    x = ninjaOffsetX.value + (NINJA_FRAME_WIDTH /2),
                    y = screenHeight - NINJA_FRAME_HEIGHT.toFloat() * 2,
                    radius =  WEAPON_SIZE,
                    //a negative sign ensures we move the ninja upward on the y axis
                    shootingSpeed = -game.settings.weaponSpeed
                )
            )
        }
    }

    //another launched effect block to spawn targets along the weapons
    //targets will spawn at a fixed interval until the game is over
    //While game is in a started state bubbled targets keep spawning every 1 and a half seconds
    LaunchedEffect(game.status){
        while(game.status == GameStatus.Started){
            delay(TARGET_SPAWN_RATE)
            //random x position that is used to randomly generate the position of the x variable i.e from zero to maximum width
            val randomX = (0..screenWidth).random()
            val isEven = (randomX % 2  == 0) //check if even
            if(isEven){
                targets.add(
                    MediumTarget(
                        x = randomX.toFloat(),
                        y = Animatable(0f), //the targets initial size
                        radius = TARGET_SIZE, //the targetSize will increase
                        fallingSpeed = game.settings.targetSpeed
                    )
                )
                //the stronger targets have a slower falling speed and spawn at right side of the screen
            } else if( randomX > screenWidth * 0.75){ targets.add(
                StrongTarget(
                    x = randomX.toFloat(),
                    y = Animatable(0f),
                    radius = TARGET_SIZE,
                    fallingSpeed = game.settings.targetSpeed * 0.25f
                )
            )

            } else {
                targets.add(
                    EasyTarget(
                        x = randomX.toFloat(),
                        y = Animatable(0f),
                        radius = TARGET_SIZE,
                        fallingSpeed = game.settings.targetSpeed
                    )
                )
            }
        }
    }
    //box since we stack multiple kunailayers on top of each other
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
                                // Calculate the "Half-out" limit (Negative offset)
                                val leftLimit = -(NINJA_FRAME_WIDTH / 2).toFloat()
                                val target = (ninjaOffsetX.value - game.settings.ninjaSpeed).coerceAtLeast(leftLimit)

                                ninjaOffsetX.snapTo(target)
                            }
                        },
                        onRight = {
                            moveDirection = MoveDirection.Right
                            runningSprite.start()
                            scope.launch {
                                // Calculate the "Half-out" limit on the right
                                // ScreenWidth minus half the character width
                                val rightLimit = (screenWidth - (NINJA_FRAME_WIDTH / 2)).toFloat()
                                val target = (ninjaOffsetX.value + game.settings.ninjaSpeed).coerceAtMost(rightLimit)

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
            targets.forEach { target ->
                drawCircle(
                    color = target.color,
                    radius = target.radius,
                    center = Offset(
                        x = target.x,
                        y = target.y.value
                    )
                )
            }
            //we also need to draw the image for each movement
            //for each loop to draw weapons for each image in this list
            weapons.forEach { weapon ->
                drawImage(
                    image = kunaiImage,
                    dstOffset = IntOffset(
                        x = weapon.x.toInt(),
                        y = weapon.y.toInt()
                    )
                )

            }
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
    //this displays the current game score
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 34.dp,
                vertical = 34.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = "Score: ${game.score}",
            fontSize = MaterialTheme.typography.titleLarge.fontSize
        )
    }
    //we add a black overlay above our screen to allow users to manually start the game
    if(game.status == GameStatus.Idle){
        Column(
            modifier = Modifier
                .clickable(enabled = false) { }
                .background(Color.Black.copy(alpha = 0.7f))
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ready?",
                fontSize = MaterialTheme.typography.displayMedium.fontSize,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    game = game.copy(
                        score = 0,
                        status = GameStatus.Started)
                }
            ){
                Text(text = "Start")
            }
        }
    }
}