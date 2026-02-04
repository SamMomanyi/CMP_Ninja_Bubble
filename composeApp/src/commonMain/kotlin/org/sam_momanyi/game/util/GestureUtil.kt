package org.sam_momanyi.game.util

import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import org.sam_momanyi.game.domain.GameStatus
import kotlin.math.abs

suspend fun AwaitPointerEventScope.detectMoveGesture(
    gameStatus: GameStatus,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onFingerLifted : () -> Unit,
) {
    while (gameStatus == GameStatus.Started){
        //awaitPointer suspends until any sort of action happens on the screen e.g hover over screen using mouse , lifting of screen
        val downEvent = awaitPointerEvent()
        val initialDown = downEvent.changes.firstOrNull{it.pressed} //this checks whether it was a press
        if(initialDown == null ) continue
        //each touch id given a unique id
        val primaryPointerId = initialDown.id //an id is assigned to that touch
        //to calculate whether the user want to move right or left
        var previousPosition = initialDown.position

        //enters another loop
        while(true){
            val event =  awaitPointerEvent() //waits for another action
            val change = event.changes.firstOrNull(){ //if the action is a second finger touch or click we ignore it
                it.id == primaryPointerId
            }

            //if use stops touching the screen
            if(change == null || !change.pressed){
                onFingerLifted()
                break
            }
            //current finger position
            val currentPosition = change.position //detect
            //calculates how much the finger has moved on the x axis
            val deltaX = currentPosition.x - previousPosition.x

            //factor out tiniest changes
            if(abs(deltaX) > 2.0) {
                if (deltaX < 0) {
                    onLeft()
                } else if (deltaX > 0) {
                    onRight()
                }
                previousPosition = currentPosition
            }
            //we then update the previous position

            //we finally consume the change
            change.consume()
        }


     }
}