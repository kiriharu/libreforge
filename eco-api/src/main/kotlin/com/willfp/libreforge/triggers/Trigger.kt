package com.willfp.libreforge.triggers

import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.Holder
import com.willfp.libreforge.LibReforge
import com.willfp.libreforge.events.EffectActivateEvent
import com.willfp.libreforge.getHolders
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.Objects

abstract class Trigger(
    val id: String,
    val parameters: Collection<TriggerParameter>
) : Listener {
    protected val plugin = LibReforge.plugin

    init {
        postInit()
    }

    private fun postInit() {
        Triggers.addNewTrigger(this)
    }

    protected fun processTrigger(player: Player, data: TriggerData, forceHolders: Iterable<Holder>? = null) {
        for (holder in forceHolders ?: player.getHolders()) {
            var areMet = true
            for ((condition, config) in holder.conditions) {
                if (!condition.isConditionMet(player, config)) {
                    areMet = false
                }
            }

            if (!areMet) {
                continue
            }

            for ((effect, config, filter, triggers) in holder.effects) {
                if (NumberUtils.randFloat(0.0, 100.0) > (config.getDoubleOrNull("chance") ?: 100.0)) {
                    continue
                }

                if (!triggers.contains(this)) {
                    continue
                }

                if (!filter.matches(data)) {
                    continue
                }

                if (effect.getCooldown(player) > 0) {
                    effect.sendCooldownMessage(player)
                    continue
                }

                val activateEvent = EffectActivateEvent(player, holder, effect)
                this.plugin.server.pluginManager.callEvent(activateEvent)

                if (!activateEvent.isCancelled) {
                    effect.resetCooldown(player, config)

                    effect.handle(data, config)
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Trigger) {
            return false
        }

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}