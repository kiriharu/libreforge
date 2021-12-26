package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class EffectPotionEffect : Effect(
    "potion_effect",
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val toApply = if (config.getBool("apply_to_player")) {
            data.player ?: return
        } else {
            data.victim ?: return
        }

        toApply.addPotionEffect(
            PotionEffect(
                PotionEffectType.getByName(config.getString("effect").uppercase())
                    ?: PotionEffectType.INCREASE_DAMAGE,
                config.getInt("duration"),
                config.getInt("level") - 1,
                false,
                true,
                true
            )
        )
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getStringOrNull("effect")
            ?: violations.add(
                ConfigViolation(
                    "effect",
                    "You must specify the potion effect!"
                )
            )

        config.getIntOrNull("level")
            ?: violations.add(
                ConfigViolation(
                    "level",
                    "You must specify the effect level!"
                )
            )

        config.getIntOrNull("duration")
            ?: violations.add(
                ConfigViolation(
                    "duration",
                    "You must specify the duration!"
                )
            )

        config.getBoolOrNull("apply_to_player")
            ?: violations.add(
                ConfigViolation(
                    "apply_to_player",
                    "You must specify whether the player or victim gets the effect!"
                )
            )

        return violations
    }
}