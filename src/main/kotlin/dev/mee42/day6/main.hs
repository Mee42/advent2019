import Data.List (intersect,elemIndex)
import Data.Maybe (fromJust)


exampleInput = "COM)B\nB)C\nC)D\nD)E\nE)\nFB)G\nG)H\nD)I\nE)J\nJ)K\nK)L"

exampleData = lines exampleInput

data Planet = Planet { name_ :: String, sub :: [Planet] }

data DataIn = DataIn { myName :: String, subName :: String }

instance Show DataIn where
  show (DataIn name sub) = name ++ ")" ++ sub

instance Show Planet where
  show (Planet name sub) = name

instance Eq Planet where
  (==) (Planet name1 sub1) (Planet name2 sub2) = name1 == name2 && sub1 == sub2


--parse :: [String] -> [Planet]
parse lines = do
  let dat = map (\line -> DataIn (takeWhile (/=')') line) (tail $ dropWhile (/=')') line)) lines
  -- take the first planet,
  let basePlanets = filter (\d -> not $ elem (myName d) (map subName dat)) dat
  
  map (planet dat) $ map myName basePlanets
  --map (\b -> planet dat (myName b)) basePlanets 
  

planet :: [DataIn] -> String -> Planet
planet dat name = do
  Planet name $ map (planet dat) $ map subName $ filter ((==name) . myName) dat

orbits :: Int -> Planet -> Int
orbits already planet = (+) already $ sum $ map (orbits (already + 1)) (sub planet)

pathTo :: String -> Planet -> [Planet]
pathTo name planet = if name_ planet == name 
                       then [planet]
                     else if sub planet == []
                       then []
                     else do
                       let results = filter (/=[]) $ map (pathTo name) (sub planet)  
                       if results == [] then [] else planet : (head results)

pathTo2 :: String -> [Planet] -> [String]
pathTo2 name planets = map name_ (head $ filter (/=[]) $ map (pathTo name) planets)

answer2part :: String -> IO Int
answer2part str = do
  planets <- parse <$> lines <$> readFile str
  let santa = pathTo2 "SAN" planets
  let you   = pathTo2 "YOU" planets
  -- find the shared element
  let base = last $ filter (\x -> elem x you) santa
  let toSanta = (length santa) - (fromJust $ elemIndex base santa)
  let  toYou  = (length  you ) - (fromJust $ elemIndex base  you )
  print santa
  print you
  print base
  print toSanta
  print toYou
  return $ toSanta + toYou - 4

answer1part :: String -> IO Int
answer1part str = sum <$> (\x -> orbits 0 <$> x) <$> parse <$> lines <$> readFile str
