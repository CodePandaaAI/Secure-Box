package com.romit.securebox.util

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Generates a RoundedCornerShape for list items with different corner radii
 * based on their position in the list.
 *
 * @param index Current item index
 * @param totalItems Total number of items in the list
 * @param topRadius Corner radius for top corners (default 12.dp)
 * @param middleRadius Corner radius for middle items (default 4.dp)
 * @param bottomRadius Corner radius for bottom corners (default 12.dp)
 * @return RoundedCornerShape based on item position
 */
fun getListItemShape(
    index: Int,
    totalItems: Int,
    topRadius: Float = 12f,
    middleRadius: Float = 0f,
    bottomRadius: Float = 12f
): RoundedCornerShape {
    return when {
        // Single item - all corners rounded
        totalItems == 1 -> RoundedCornerShape(topRadius.dp)

        // First item - top corners rounded
        index == 0 -> RoundedCornerShape(
            topStart = topRadius.dp,
            topEnd = topRadius.dp,
            bottomStart = middleRadius.dp,
            bottomEnd = middleRadius.dp
        )

        // Last item - bottom corners rounded
        index == totalItems - 1 -> RoundedCornerShape(
            topStart = middleRadius.dp,
            topEnd = middleRadius.dp,
            bottomStart = bottomRadius.dp,
            bottomEnd = bottomRadius.dp
        )

        // Middle items - small radius on all corners
        else -> RoundedCornerShape(middleRadius.dp)
    }
}