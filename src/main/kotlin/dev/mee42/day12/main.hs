import Debug.Trace

data Moon = Moon { xPos :: Int, yPos :: Int, zPos :: Int,
                    xVel :: Int, yVel :: Int, zVel :: Int }
                    deriving Eq
showPos :: (Int,Int,Int) -> String
showPos (a,b,c) = "<" ++ show a ++ "," ++ show b ++ "," ++ show c ++ ">"

instance Show Moon where
  show (Moon xP yP zP xV yV zV)= "pos=" ++ showPos (xP,yP,zP) ++ " vel=" ++ showPos (xV,yV,zV)



addVelocityMoon :: Moon -> (Int, Int, Int) -> Moon
addVelocityMoon (Moon xPos yPos zPos xVel yVel zVel) (xVel1,yVel1,zVel1) = Moon xPos yPos zPos (xVel+xVel1) (yVel+yVel1) (zVel+zVel1)

newVel :: Moon -> Moon -> (Moon -> Int) -> Int
newVel moon otherMoon func = if (func moon) > (func otherMoon) then -1
                             else if(func moon) < (func otherMoon) then 1
                             else 0
addVel :: (Int,Int,Int) -> (Int,Int,Int) -> (Int,Int,Int)
addVel (a,b,c) (a',b',c') = (a+a',b+b',c+c')


applyNewVel :: Moon -> (Int,Int,Int) -> Moon
applyNewVel (Moon xP yP zP _ _ _) (xV, yV, zV) = Moon (xP + xV) (yP + yV) (zP + zV) xV yV zV


calculateNewVel :: [Moon] -> Moon -> Moon
calculateNewVel allMoons moon = applyNewVel moon $ 
       foldr (\oth total -> addVel (newVel moon oth xPos, newVel moon oth yPos, newVel moon oth zPos) total) (xVel moon, yVel moon, zVel moon) allMoons


step :: [Moon] -> [Moon]
step arr = map (calculateNewVel arr) arr

energy :: [Moon] -> Int
energy [] = 0
energy ((Moon a b c d e f):xs) = (abs a + abs b + abs c)*(abs d + abs e + abs f) +  (energy xs)



answer2brute :: [Moon] -> [Moon] -> Int -> Int
answer2brute inital next i = do
  let x = if i `mod` 1000 == 0 then trace ("runing " ++ show i) 1 else 1
  if inital == next then i + 1 else x * answer2brute inital (step next) (i + 1)


answer1 :: [Moon] -> Int -> Int
answer1 moons index = if index == 0 then energy moons else answer1 (step moons) (index-1)


answer2' :: [Moon] -> Int
answer2' moons = do
  let (a,b,c) = answer2'' moons
  lcm (lcm a b) c

data MiniMoon = MiniMoon { pos :: Int, vel :: Int } deriving (Show, Eq)

answer2'' :: [Moon] -> (Int,Int,Int)
answer2'' ms = do
  let miniX = map (\mn -> MiniMoon (xPos mn) 0) ms
  let miniY = map (\mn -> MiniMoon (yPos mn) 0) ms
  let miniZ = map (\mn -> MiniMoon (zPos mn) 0) ms
  (answer2mini miniX, answer2mini miniY, answer2mini miniZ)

 
answer2mini :: [MiniMoon] -> Int
answer2mini inital = answer2mini' inital (miniStep inital)

answer2mini' :: [MiniMoon] -> [MiniMoon] -> Int
answer2mini' inital next = if inital == next then 1 else 1 + answer2mini' inital (miniStep next)

miniStep :: [MiniMoon] -> [MiniMoon]
miniStep arr = map (calculateNewVel' arr) arr

applyNewVel' :: MiniMoon -> Int -> MiniMoon
applyNewVel' (MiniMoon pos' _) vel' = MiniMoon (pos' + vel') vel'


newVel' :: MiniMoon -> MiniMoon -> Int
newVel'  moon otherMoon = if pos moon > pos otherMoon then -1
                          else if pos moon < pos otherMoon then 1
                          else 0


calculateNewVel' :: [MiniMoon] -> MiniMoon -> MiniMoon
calculateNewVel' allMoons moon = applyNewVel' moon $
         foldr (\other total -> total + (newVel' moon other)) (vel moon) allMoons




