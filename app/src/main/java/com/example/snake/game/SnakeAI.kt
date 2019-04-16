package com.example.snake.game

import android.graphics.Color
import com.example.snake.extensions.random
import kotlin.math.abs

class SnakeAI(
    _tiles: List<Tile>,
    _limit1: Tile,
    _limit2: Tile) :
    Snake(
    tiles = _tiles,
    direction = Direction.LEFT,
    color = Color.BLUE,
    limit1 = _limit1,
    limit2 = _limit2
) {

    /**
     * This compute the distance between the head snake and the apple
     * It apply the appropriate direction depends on previous calculation (vector snake-apple)
     * Before setting the new direction, it check that new direction will not hit something
     * */
    fun findApple(apple: Apple) {
        // vector snake apple
        val vSAx = apple.position.x - head.x
        val vSAy = apple.position.y - head.y

        var nextDir:Direction = Direction.UP
        if (abs(vSAx) > abs(vSAy)) {
            if (vSAx < 0) {
                nextDir = Direction.LEFT
            }
            if (vSAx > 0) {
                nextDir = Direction.RIGHT
            }
        } else {
            if (vSAy < 0) {
                nextDir = Direction.UP
            }
            if (vSAy > 0) {
                nextDir = Direction.DOWN
            }
        }
        if (willHitSomething(nextDir)) {
            Direction.values().find {
                !willHitSomething(it)
            }?.let { nextDir = it }
        }
        direction = nextDir
    }

    private fun willHitSomething(nextDir: Direction): Boolean {
        return willHitHimself(nextDir) || willHitBounds(nextDir)
    }

    private fun willHitHimself(nextDir: Direction): Boolean {
        tail.find {
            it.x == head.x + nextDir.x && it.y == head.y + nextDir.y
        }?: return false
        return true
    }

    private fun willHitBounds(nextDir: Direction): Boolean {
        return (
                head.x + nextDir.x < limit1.x || head.x + nextDir.x > limit2.x - size
                ) || (
                head.y + nextDir.y < limit1.y || head.y + nextDir.y > limit2.y - size)
    }

}