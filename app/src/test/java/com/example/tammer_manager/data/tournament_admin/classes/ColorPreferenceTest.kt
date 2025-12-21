package com.example.tammer_manager.data.tournament_admin.classes


import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ColorPreferenceTest {
    @Test
    fun `weaker strength is less than stronger strength`() {
        assertThat(
            ColorPreference(
                strength = ColorPreferenceStrength.MILD,
                colorBalance = 0
            )
        ).isLessThan(
            ColorPreference(
                strength = ColorPreferenceStrength.STRONG,
                colorBalance = 1
            )
        )
    }

    @Test
    fun `same strength lower absolute color balance is less`() {
        assertThat(
            ColorPreference(
                strength = ColorPreferenceStrength.ABSOLUTE,
                colorBalance = 2
            )
        ).isLessThan(
            ColorPreference(
                strength = ColorPreferenceStrength.ABSOLUTE,
                colorBalance = 3
            )
        )
    }

    @Test
    fun `color balance sign does not matter`() {
        assertThat(
            ColorPreference(
                strength = ColorPreferenceStrength.STRONG,
                colorBalance = -2
            )
        ).isEquivalentAccordingToCompareTo(
            ColorPreference(
                strength = ColorPreferenceStrength.STRONG,
                colorBalance = 2
            )
        )
    }

    @Test
    fun `strength comparison takes priority over color balance`() {
        assertThat(
            ColorPreference(
                strength = ColorPreferenceStrength.MILD,
                colorBalance = 100
            )
        ).isLessThan(
            ColorPreference(
                strength = ColorPreferenceStrength.STRONG,
                colorBalance = 0
            )
        )
    }

    @Test
    fun `equal strength and equal absolute balance are equal`() {
        assertThat(
            ColorPreference(
                strength = ColorPreferenceStrength.NONE,
                colorBalance = -1
            )
        ).isEquivalentAccordingToCompareTo(
            ColorPreference(
                strength = ColorPreferenceStrength.NONE,
                colorBalance = 1
            )
        )
    }

    @Test
    fun `preferred color does not affect comparison`() {
        assertThat(
            ColorPreference(
                strength = ColorPreferenceStrength.STRONG,
                colorBalance = 2,
                preferredColor = PlayerColor.WHITE
            )
        ).isEquivalentAccordingToCompareTo(
            ColorPreference(
                strength = ColorPreferenceStrength.STRONG,
                colorBalance = -2,
                preferredColor = PlayerColor.BLACK
            )
        )
    }

    @Test
    fun `greater absolute color balance is greater when strength equal`() {
        assertThat(
            ColorPreference(
                strength = ColorPreferenceStrength.ABSOLUTE,
                colorBalance = 3
            )
        ).isGreaterThan(
            ColorPreference(
                strength = ColorPreferenceStrength.ABSOLUTE,
                colorBalance = 2
            )
        )
    }

}