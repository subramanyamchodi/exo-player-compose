package com.sample.player.video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.delay
import kotlin.random.Random

private val FAB_RADIUS = 125 / 2f

// offset of the first control point (top part)
private val TOP_CONTROL_X = FAB_RADIUS + FAB_RADIUS / 2
private val TOP_CONTROL_Y = FAB_RADIUS / 6

// offset of the second control point (bottom part)
private val BOTTOM_CONTROL_X = FAB_RADIUS + (FAB_RADIUS / 2)
private val BOTTOM_CONTROL_Y = FAB_RADIUS / 4

// width of the curve
private val CURVE_OFFSET = FAB_RADIUS * 2 + (FAB_RADIUS / 2)
private val WIDTH_FACTOR = 0.10f.plus(2 * 0.20f)

// first bezier curve
val firstCurveStart = Point()
val firstCurveEnd = Point()
val firstCurveControlPoint1 = Point()
val firstCurveControlPoint2 = Point()

// second bezier curve
val secondCurveStart = Point()
val secondCurveEnd = Point()
val secondCurveControlPoint1 = Point()
val secondCurveControlPoint2 = Point()

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomCutoutShape(
    modifier: Modifier
) {
    var size by remember { mutableStateOf(Size.Zero) }
    var position by remember { mutableStateOf(2) }
    var fabClick by remember { mutableStateOf(0) }
    var fabVisible by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = fabClick) {
        fabVisible = false
        delay(500)
        fabVisible = true
        position = Random.nextInt(4)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Gray)
    ) {
        Box(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
//                .clip(if (fabVisible) cutoutShape(position = position) else CutCornerShape(0.dp))
                .background(color = Color.Blue, cutoutShape(position = position))
                .onGloballyPositioned {
                    size = it.size.toSize()
                },
        ) {

        }
        Text(text = "count $position")
        val coordinates = when (position) {
            0 -> -(size.width / (2.5f * 1)) // Position 1
            1 -> -(size.width / (2.5f * 2)) // Position 2
            2 -> 0f // Position 3 (centered)
            3 -> (size.width / (2.5f * 2)) // Position 4
            4 -> (size.width / (2.5f * 1)) // Position 5
            else -> {
                position = 0
                0f
            }
        }
        val widthFactor by animateFloatAsState(targetValue = coordinates)

        AnimatedVisibility(
            modifier = Modifier
                .padding(bottom = 25.dp)
                .align(Alignment.BottomCenter)
                .graphicsLayer {
//                    translationX = when (position) {
//                        0 -> -(size.width / (2.5f * 1)) // Position 1
//                        1 -> -(size.width / (2.5f * 2)) // Position 2
//                        2 -> 0f // Position 3 (centered)
//                        3 -> (size.width / (2.5f * 2)) // Position 4
//                        4 -> (size.width / (2.5f * 1)) // Position 5
//                        else -> {
//                            position = 0
//                            0f
//                        }
//                    }
                               translationX = widthFactor
                },
            visible = fabVisible,
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            FloatingActionButton(
//                modifier = Modifier
//                    .padding(bottom = 25.dp)
//                    .align(Alignment.BottomCenter)
//                    .graphicsLayer {
//                        translationX = when (position) {
//                            0 -> -(size.width / (2.5f * 1)) // Position 1
//                            1 -> -(size.width / (2.5f * 2)) // Position 2
//                            2 -> 0f // Position 3 (centered)
//                            3 -> (size.width / (2.5f * 2)) // Position 4
//                            4 -> (size.width / (2.5f * 1)) // Position 5
//                            else -> {
//                                position = 0
//                                0f
//                            }
//                        }
//                    },
                onClick = {
                    fabClick += 1
                },
                elevation = FloatingActionButtonDefaults.elevation(5.dp)
            ) {

            }
        }

        // Content inside the cutout shape
    }
}

@Composable
fun cutoutShape(
    position: Int
): Shape {

    val widthFactor by remember(position) {
        mutableStateOf(0.10f.plus(position * 0.20f))
    }
//    val widthFactor by animateFloatAsState(targetValue = 0.10f.plus(position * 0.20f))

    return object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            firstCurveStart.apply {
                // we want the curve to start at CURVE_OFFSET before the center of the view
                x = ((size.width * widthFactor) - CURVE_OFFSET)
                y = 0f
            }
            // set the end point for the first curve (P3)
            firstCurveEnd.apply {
                x = (size.width * widthFactor)
                y = FAB_RADIUS + (FAB_RADIUS / 2)
            }
            // set the first control point (C1)
            firstCurveControlPoint1.apply {
                x = firstCurveStart.x + TOP_CONTROL_X
                y = TOP_CONTROL_Y
            }
            // set the second control point (C2)
            firstCurveControlPoint2.apply {
                x = firstCurveEnd.x - BOTTOM_CONTROL_X
                y = firstCurveEnd.y - BOTTOM_CONTROL_Y
            }

            // second curve
            // end of first curve and start of second curve is the same (P3)
            secondCurveStart.apply {
                x = firstCurveEnd.x
                y = firstCurveEnd.y
            }
            // end of the second curve (P4)
            secondCurveEnd.apply {
                x = (size.width * widthFactor) + CURVE_OFFSET
                y = 0f
            }
            // set the first control point of second curve (C4)
            secondCurveControlPoint1.apply {
                x = secondCurveStart.x + BOTTOM_CONTROL_X
                y = secondCurveStart.y - BOTTOM_CONTROL_Y
            }
            // set the second control point (C3)
            secondCurveControlPoint2.apply {
                x = secondCurveEnd.x - TOP_CONTROL_X
                y = TOP_CONTROL_Y
            }

            val path = Path().apply {
                reset()
                // start from P1 of the BottomNavigationView
                moveTo(0f, 0f)
                lineTo(firstCurveStart.x, firstCurveStart.y)
                cubicTo(
                    firstCurveControlPoint1.x,
                    firstCurveControlPoint1.y,
                    firstCurveControlPoint2.x,
                    firstCurveControlPoint2.y,
                    firstCurveEnd.x,
                    firstCurveEnd.y
                )
                cubicTo(
                    secondCurveControlPoint1.x,
                    secondCurveControlPoint1.y,
                    secondCurveControlPoint2.x,
                    secondCurveControlPoint2.y,
                    secondCurveEnd.x,
                    secondCurveEnd.y
                )
                lineTo(size.width, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            return Outline.Generic(path)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewCutOut() {
    BottomCutoutShape(
        modifier = Modifier.fillMaxSize()
    )
}

data class Point(
    var x: Float = 0f,
    var y: Float = 0f
)


