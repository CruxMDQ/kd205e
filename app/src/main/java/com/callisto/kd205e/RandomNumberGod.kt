package com.callisto.kd205e

class RandomNumberGod
{
    companion object
    {
        fun roll4d6MinusLowest(): Int
        {
            val rolls = arrayOf((1..6).random(), (1..6).random(), (1..6).random(), (1..6).random())

            return rolls.sum() - rolls.min()!!
        }

        fun rollD20(): Int
        {
            @Suppress("JoinDeclarationAndAssignment")
            val iResult : Int

            iResult = (1..20).random()

            return iResult
        }
    }
}