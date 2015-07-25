require_relative '../bowling'
include Bowling

describe Game do
  let (:game) {game = Game.new}

  def roll_many(n, pins)
    n.times do
      game.roll(pins)
    end
  end

  def roll_strike
    game.roll(10)
  end

  def roll_spare
    game.roll(5)
    game.roll(5)
  end

  it 'can roll' do
    game.roll(0)
  end

  it "can roll all gutter balls" do
    roll_many(20, 0)
    game.score.should == 0
  end

  it "can roll all ones" do
    roll_many(20, 1)
    game.score.should == 20
  end

  it "can roll a spare" do
    roll_spare
    game.roll(3)
    roll_many(17, 0)
    game.score.should == 16
  end

  it 'can roll a strike' do
    roll_strike
    game.roll(3)
    game.roll(4)
    roll_many(16, 0)
    game.score.should == 24
  end

  # "XXXXXXXXXXXX"
  it 'can roll a perfect game' do
    roll_many(12, 10)
    game.score.should == 300
  end

  # "9-9-9-9-9-9-9-9-9-9-"
  it 'can roll 10 frames of nine-miss' do
    10.times do
      game.roll(9)
      game.roll(0)
    end
    game.score.should == 90
  end

  # "5/5/5/5/5/5/5/5/5/5/5"
  it 'can roll 10 frames of five-spare' do
    roll_many(21, 5)
    game.score.should == 150
  end

  it 'can roll 10 frames of strike-spare' do
    5.times do
      roll_strike
      roll_spare
    end
    roll_strike
    game.score.should == 200
  end

  it 'can roll 10 frames of spare-strike-strike' do
    3.times do
      roll_spare
      roll_strike
      roll_strike
    end
    roll_spare
    roll_strike
    game.score.should == 215
  end

end
