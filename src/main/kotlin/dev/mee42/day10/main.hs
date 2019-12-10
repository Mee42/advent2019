
inputStr = ".#..#\n.....\n#####\n....#\n...##"

parseInput :: String -> [[Bool]]
parseInput = (map (map (=='#'))) . lines

input :: [[Bool]]
input = parseInput inputStr


at :: [[a]] -> (Int,Int) -> a
at arr (x,y) = (arr !! x) !! y

astroides :: [[Bool]] -> [(Int,Int)]
astroides arr = filter (at arr) $ indexes arr

indexes :: [[a]] -> [(Int,Int)]
indexes arr = [(x,y) | x <- [0..(length arr - 1)], y <- [0..(length (arr!!0) -1)]]  

count :: [[Bool]] -> (Int,Int) -> [(Int,Int)]
count array (x,y) = if not $ at array (x,y) then []
  else  filter (\(a,b) -> a /= x || b /= y) $ filter (\pair ->  
          (intercept (astroides array) (interceptPointsBetween (x,y) pair)) == []
         ) $ astroides array
  

intercept :: Eq a => [a] -> [a] -> [a]
intercept arr1 arr2 = [el | el <- arr1, elem el arr2]

between :: Int -> Int -> [Int]
between a b = if a == b then []
              else if a > b then [(b+1)..(a-1)] else [(a+1)..(b-1)]

interceptPointsBetween :: (Int,Int) -> (Int,Int) -> [(Int,Int)]
interceptPointsBetween (x1,y1) (x2, y2) = do
  if x1 == x2 && y1 == y2 then []
  else if x1 == x2 then [(x1,y) | y <- between y1 y2]
  else if y1 == y2 then [(x,y1) | x <- between x1 x2]
  else do
    let allX = between x1 x2
    let xA = toRational (x2 - x1)
    let yA = toRational (y2 - y1)
    let m = 1/(xA / yA)
    let b = toRational y1 - m * toRational x1
    let filteredX = filter (\x -> m * toRational x + b == 
                             toRational (truncate (m * toRational x + b))) allX
    let filteredY = (\x -> truncate (m * toRational x + b)) <$> filteredX
    zip filteredX filteredY

--answer1 :: [[Bool]] -> Int
answer1 arr = maximum $ map (\point -> length (count arr point)) $ indexes arr

debug :: [[Bool]] -> IO ()
debug arr = sequence_ $ (map (\i -> debug2 i arr) [0..(length arr -1)])

debug2 :: Int -> [[Bool]] -> IO ()
debug2 x arr = do 
    sequence_ $ map (\y -> 
         (putStr.(padUpTo 10).show.length.(count arr)) (x,y)
     ) [0..length (arr!!x) - 1]
    putStrLn ""
    return ()

padUpTo :: Int -> String -> String
padUpTo i str = if length str >= i then str else padUpTo i (str++" ")



