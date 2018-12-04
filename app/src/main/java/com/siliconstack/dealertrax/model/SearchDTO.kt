package com.siliconstack.dealertrax.model

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class SearchDTO(title: String, items: List<MainDTO>) : ExpandableGroup<MainDTO>(title, items)