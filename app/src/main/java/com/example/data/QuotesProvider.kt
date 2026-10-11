package com.example.data

import com.example.data.model.DailyQuote

object QuotesProvider {
    val quotes = listOf(
        DailyQuote("We are what we repeatedly do. Excellence, then, is not an act, but a habit.", "Aristotle"),
        DailyQuote("You do not rise to the level of your goals. You fall to the level of your systems.", "James Clear"),
        DailyQuote("Small disciplines repeated with consistency every day lead to great achievements.", "John C. Maxwell"),
        DailyQuote("The secret of getting ahead is getting started.", "Mark Twain"),
        DailyQuote("Success is the product of daily habits—not once-in-a-lifetime transformations.", "James Clear"),
        DailyQuote("It does not matter how slowly you go as long as you do not stop.", "Confucius"),
        DailyQuote("First forget inspiration. Habit is more dependable. Habit will sustain you whether you're inspired or not.", "Octavia Butler"),
        DailyQuote("Motivation is what gets you started. Habit is what keeps you going.", "Jim Ryun"),
        DailyQuote("You’ll never change your life until you change something you do daily.", "John C. Maxwell"),
        DailyQuote("Continuous improvement is better than delayed perfection.", "Mark Twain"),
        DailyQuote("Discipline is choosing between what you want now and what you want most.", "Abraham Lincoln"),
        DailyQuote("Habits change into character.", "Ovid"),
        DailyQuote("The chains of habit are too weak to be felt until they are too strong to be broken.", "Samuel Johnson"),
        DailyQuote("Action is the foundational key to all success.", "Pablo Picasso"),
        DailyQuote("Consistency is what transforms average into excellence.", "Tony Robbins"),
        DailyQuote("Do something today that your future self will thank you for.", "Sean Patrick Flanery"),
        DailyQuote("Energy flows where attention goes.", "Tony Robbins"),
        DailyQuote("Winning is a habit. Unfortunately, so is losing.", "Vince Lombardi"),
        DailyQuote("You don't have to be extreme, just consistent.", "Unknown"),
        DailyQuote("A year from now you may wish you had started today.", "Karen Lamb"),
        DailyQuote("Focus on the process, not the outcome.", "Epictetus"),
        DailyQuote("Waste no more time arguing what a good man should be. Be one.", "Marcus Aurelius"),
        DailyQuote("The only bad workout is the one that didn't happen.", "Unknown"),
        DailyQuote("Simplicity boil down to two steps: Identify the essential. Eliminate the rest.", "Leo Babauta"),
        DailyQuote("Step by step. I can't see any other way of accomplishing anything.", "Michael Jordan"),
        DailyQuote("Your habits will determine your future.", "Jack Canfield")
    )

    fun getQuoteForDate(dateString: String, offset: Int = 0): DailyQuote {
        val index = (Math.abs(dateString.hashCode() + offset)) % quotes.size
        return quotes[index]
    }
}
