import Data.List


unzipWith :: (a -> b -> c) -> [(a,b)] -> [c]
unzipWith _ [] = []
unzipWith com ((a,b):xs) = (com a b) : (unzipWith com xs)


numOut :: Int -> [Int]
numOut i = map (\x -> read [x] :: Int) (show i)

isValid :: Int -> Bool
isValid int = do
  let nums = map (\x -> (read [x]) :: Int) (show int) -- [Int]
  (any id (unzipWith (==) (nums `zip` tail nums))) &&
    (all id (unzipWith (<=) (nums `zip` tail nums)))



isValid2 :: Int -> Bool
isValid2 int = do
  let nums = numOut int 
 
  let triples = concat $ map (\(index,(a,_,_)) -> [index,index+1,index+2]) $ filter (\(_,(a,b,c)) -> a==b&&b==c) $ zip [0..] $ zip3 nums (drop 1 nums) (drop 2 nums)
  
  
  let pairs = filter (\(index,(first,second)) -> not (elem index triples) && first == second) $ zip [0..] $ nums `zip` (drop 1 nums) 

  pairs /= [] && (all id (unzipWith (<=) (nums `zip` tail nums)))


input :: [Int]
input = [367479..893698]

part1 :: Int
part1 = length $ filter isValid input

part2 :: Int
part2 = length $ filter isValid2 input
