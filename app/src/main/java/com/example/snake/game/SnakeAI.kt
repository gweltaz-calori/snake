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
     * Before setting the new direction, it check that new direction is not invalid and that the snake will not hit bounds
     * */
    fun findApple(apple: Apple) {
        // vector snake apple
        val vSAx = apple.position.x - head.x
        val vSAy = apple.position.y - head.y

        var nextDir = Direction.STOP
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
        if (nextDirIsInvalid(direction, nextDir)) {
            nextDir = computeCircumvention(nextDir)
        }
        if (willHitBounds(nextDir)) {
            nextDir = computeCircumvention(nextDir)
        }
        direction = nextDir
    }

    /* fun willHitHimself(nextDir: Direction) {

    } */

    // TODO: delete this and replace it by snake detection of itself
    fun nextDirIsInvalid(prevDir: Direction, nextDir: Direction): Boolean {
        // this check that the snake doesn't do half turn
        return nextDir.x != prevDir.x && nextDir.x + prevDir.x == 0
            || nextDir.y != prevDir.y && nextDir.y + prevDir.y == 0
    }

    // TODO: refactor this to make a smarter calculation
    fun computeCircumvention(nextDir: Direction): Direction {
        if (nextDir.x == 0) {
            // snake is going up or down and need to go left or right
            if (random(0, 2) < 1) return Direction.RIGHT
            return Direction.LEFT
        }
        // snake is going left or right and need to go up or down
        if (random(0, 2) < 1) return Direction.UP
        return Direction.DOWN
    }

    fun willHitBounds(nextDir: Direction): Boolean {
        tiles.find {
            (it.x + nextDir.x < limit1.x || it.x + nextDir.x > limit2.x - size)
            || (it.y + nextDir.y < limit1.y || it.y + nextDir.y > limit2.y - size) }
            ?: return false
        return true
    }

}