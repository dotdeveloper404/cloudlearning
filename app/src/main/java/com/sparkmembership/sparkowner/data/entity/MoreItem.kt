package com.sparkmembership.sparkowner.data.entity

import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.SparkOwner.Companion.ctx
import com.sparkmembership.sparkowner.data.enums.ItemTypeMore

data class MoreItem(
    val title: String,
    val iconResId: Int?,
    val type: ItemTypeMore,
)

val moreItems = listOf(
    MoreItem(ctx.getString(R.string.location), null, ItemTypeMore.HEADER),
    MoreItem(ctx.getString(R.string.change_location), R.drawable.icon_location, ItemTypeMore.LIST_ITEM),
    MoreItem(ctx.getString(R.string.contacts), null, ItemTypeMore.HEADER),
    MoreItem(ctx.getString(R.string.quickly_add_a_contact), R.drawable.icon_quick_add_contact, ItemTypeMore.LIST_ITEM),
    MoreItem(ctx.getString(R.string.view_contacts), R.drawable.icon_view_contact, ItemTypeMore.LIST_ITEM),
    MoreItem(ctx.getString(R.string.task), null, ItemTypeMore.HEADER),
    MoreItem(ctx.getString(R.string.tasks), R.drawable.icon_task, ItemTypeMore.LIST_ITEM),

    MoreItem(ctx.getString(R.string.calendar), null, ItemTypeMore.HEADER),
    MoreItem(ctx.getString(R.string.schedule), R.drawable.icon_schedule, ItemTypeMore.LIST_ITEM),
    MoreItem(ctx.getString(R.string.calendar_type), R.drawable.icon_calendar_type, ItemTypeMore.LIST_ITEM),

    MoreItem(ctx.getString(R.string.reports), null, ItemTypeMore.HEADER),
    MoreItem(ctx.getString(R.string.invoice_history), R.drawable.icon_invoice, ItemTypeMore.LIST_ITEM),
    MoreItem(ctx.getString(R.string.view_time_slips), R.drawable.icon_view_contact, ItemTypeMore.LIST_ITEM),
    MoreItem(ctx.getString(R.string.attendance_reports), R.drawable.icon_attendace, ItemTypeMore.LIST_ITEM),
    MoreItem(ctx.getString(R.string.current_stripes), R.drawable.icon_current_stripe, ItemTypeMore.LIST_ITEM),
    MoreItem(ctx.getString(R.string.nav_clock_in_out), R.drawable.icon_current_stripe, ItemTypeMore.LIST_ITEM),
    MoreItem(ctx.getString(R.string.nav_who_is_in), R.drawable.icon_current_stripe, ItemTypeMore.LIST_ITEM),

)
