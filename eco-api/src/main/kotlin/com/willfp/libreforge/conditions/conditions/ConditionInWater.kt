package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionInWater: Condition("in_water") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.isInWater == config.getBool("in_water")
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getBoolOrNull("in_water")
            ?: violations.add(
                ConfigViolation(
                    "in_water",
                    "You must specify if the player must be in water or not!"
                )
            )

        return violations
    }
}