package com.apps.leo.clicker.game.common.ui

import org.junit.Assert
import org.junit.Test

class MoneyFormatterTest {

    @Test
    fun `zero should be 0`() {
        //Arrange
        val expected = "$0"
        val amount = 0L

        //Act
        val result = formatAmountOfMoney(amount)

        //Assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun `1-999 should be itself`() {
        //Arrange
        val expected = "$123"
        val amount = 123L

        //Act
        val result = formatAmountOfMoney(amount)

        //Assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun `Thousands should be with K`() {
        //Arrange
        val expected = "$123.45 K"
        val amount = 123_450L

        //Act
        val result = formatAmountOfMoney(amount)

        //Assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun `Millions should be with M`() {
        //Arrange
        val expected = "$123.45 M"
        val amount = 123_450_000L

        //Act
        val result = formatAmountOfMoney(amount)

        //Assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun `Trillions should be with T`() {
        //Arrange
        val expected = "$123.45 T"
        val amount = 123_450_000_000L

        //Act
        val result = formatAmountOfMoney(amount)

        //Assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun `Quadrillions should be with Q`() {
        //Arrange
        val expected = "$123.45 Q"
        val amount = 123_450_000_000_000L

        //Act
        val result = formatAmountOfMoney(amount)

        //Assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun `Quintillions should be with Qi`() {
        //Arrange
        val expected = "$123.45 Qi"
        val amount = 123_450_000_000_000_000L

        //Act
        val result = formatAmountOfMoney(amount)

        //Assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun `Should round correctly`() {
        //Arrange
        val expected = "$123.46 K"
        val amount = 123_459L

        //Act
        val result = formatAmountOfMoney(amount)

        //Assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun `Should not show zeros at the end`() {
        //Arrange
        val expected = "$123 K"
        val amount = 123_000L

        //Act
        val result = formatAmountOfMoney(amount)

        //Assert
        Assert.assertEquals(expected, result)
    }

    //todo add tests with new parameters
}