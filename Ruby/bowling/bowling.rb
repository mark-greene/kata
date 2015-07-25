#!/usr/bin/env ruby
require 'optparse'

module Bowling

class Game

  def initialize
     @rolls = []
  end

  def roll(pins)
      @rolls << pins
  end

  def score
    score = 0
    first_in_frame = 0
    frame = 0
    while frame < 10
      if strike?(first_in_frame)
        score += 10 + strike_bonus_balls(first_in_frame)
        first_in_frame +=1
      elsif spare?(first_in_frame)
        score += 10 + spare_bonus_ball(first_in_frame)
        first_in_frame += 2
      else
        score += two_balls_in_frame(first_in_frame)
        first_in_frame += 2
      end
      frame +=1
    end
    score
  end

  def strike_bonus_balls(first_in_frame)
    @rolls[first_in_frame + 1] + @rolls[first_in_frame + 2]
  end

  def spare_bonus_ball(first_in_frame)
    @rolls[first_in_frame + 2]
  end

  def two_balls_in_frame(first_in_frame)
    @rolls[first_in_frame] + @rolls[first_in_frame + 1]
  end

  def spare?(first_in_frame)
    @rolls[first_in_frame] + @rolls[first_in_frame + 1] == 10
  end

  def strike?(first_in_frame)
    @rolls[first_in_frame] == 10
  end

end


OptionParser.new do |o|
  o.banner = 'Usage: ruby bowling.rb --name <your name> --score <your score>'
  o.separator  ""

  o.on('-n ', '--name "name"', 'Your name: "Mark"') { |name| $name = name }

  o.on('-s score', '--score "score"', 'Your score card: "XXX9-817263549/3/X"') { |score| $score = score }

  o.on('-h', '--help', 'Show this message') { puts o;  exit }

  o.parse!
end

begin
  if !$score.nil?
    game = Game.new

    $score.each_char.with_index { |ball, index|
      # puts "#{ball},  #{index}"
      if ball.upcase == 'X'
        game.roll(10)
      elsif ball == '/'
        game.roll(10 - $score[index-1].to_i)
      elsif ball == '-'
        game.roll(0)
      else
        game.roll(ball.to_i)
      end
    }

   $name = $name.nil? && 'You'  ||  $name
    puts "#{$name} bowled #{game.score}"
  end
rescue
  puts 'Invalid score'
end

end
