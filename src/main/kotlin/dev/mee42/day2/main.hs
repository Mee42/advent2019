import Debug.Trace

baseInput :: [Int]
baseInput = [1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,13,1,19,1,19,9,23,1,5,23,27,1,27,9,31,1,6,31,35,2,35,9,39,1,39,6,43,2,9,43,47,1,47,6,51,2,51,9,55,1,5,55,59,2,59,6,63,1,9,63,67,1,67,10,71,1,71,13,75,2,13,75,79,1,6,79,83,2,9,83,87,1,87,6,91,2,10,91,95,2,13,95,99,1,9,99,103,1,5,103,107,2,9,107,111,1,111,5,115,1,115,5,119,1,10,119,123,1,13,123,127,1,2,127,131,1,131,13,0,99,2,14,0,0]

setInput :: Int -> Int -> [Int]
setInput noun verb = 1 : noun : verb : (drop 3 baseInput)


pairs :: [(Int,Int)]
pairs = [(a,b) | a <- [0..99], b <- [0..99]]

 

set :: [a] -> Int -> a -> [a]
set arr index value = (take index arr) ++ [value] ++ (drop (index + 1) arr)

iter :: Int -> [Int] -> [Int]
iter index arr | arr !! index == 99 = arr
               | arr !! index == 1 = opperation arr index (+)
               | arr !! index == 2 =  opperation arr index (*)
               | otherwise = arr
               where g arr int = arr !! ( arr!! int)
                     opperation arr int op = iter (index+4) $ set arr (arr!!(index+3)) (op (g arr(index+1)) (g arr (index+2)))


part2answer :: (Int,Int)
part2answer = head $ filter (\(a,b) -> iter 0 (setInput a b) !! 0 == 19690720) pairs
