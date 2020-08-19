package com.zii.whatsappclone.listener

import com.zii.whatsappclone.model.StatusListElement

interface StatusItemClickListener {
    fun onItemClicked(statusElement: StatusListElement)
}