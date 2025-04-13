import { Card, CardContent, Divider, Box, Typography } from '@mui/material';
import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router'
import { getActivityDetail } from '../services/api'
// import { Box, Typography } from '@mui/material';
const ActivityDetail = () => {
    const { id } = useParams();
    const [activity, setActivity] = useState(null);
    const [recommendation, setRecommendation] = useState(null);
    useEffect(() => {
        const fetchActivityDetail = async () => {
            try {
                const response = await getActivityDetail(id);
                setActivity(response.data);
                setRecommendation(response.data.recommendation);
            } catch (error) {
                console.error(error);
            }
        }

        fetchActivityDetail();


    }, [id]);
    if (!activity) {
        return (
            <Box sx={{ maxWidth: 800, mx: 'auto', p: 2 }}>
                <Typography>Loading...</Typography>
            </Box>
        )
    }
    return (
        <Box sx={{ maxWidth: 800, mx: 'auto', p: 2 }}>
            <Card sx={{ mb: 2 }}>
                <CardContent>
                    <Typography variant="h5" gutterBottom>Activity Details</Typography>
                    <Typography>Type: {activity.activityType}</Typography>
                    {/* <Typography>Duration: {activity.duration} minutes</Typography> */}
                    <Typography>
                        Duration: {activity ? activity.duration : 'Unknown'} minutes
                    </Typography>
                    <Typography>Calories Burned: {activity ? activity.activityType : 'Unknown'}</Typography>
                    <Typography>Date: {new Date(activity.createdAt).toLocaleString()}</Typography>
                </CardContent>
            </Card>
            {
                recommendation && (
                    <Card>
                        <CardContent>
                            <Typography variant="h5" gutterBottom>AI Recommendation</Typography>
                            <Typography variant="h6">Analysis</Typography>
                            <Typography paragraph>{activity.recommendation}</Typography>
                            <Divider sx={{ my: 2 }} />

                            <Typography variant="h6">Improvements</Typography>
                            {activity?.improvements?.map((improvements, index) => (
                                <Typography key={index} paragraph> {improvements}</Typography>
                            ))}

                            <Divider sx={{ my: 2 }} />

                            <Typography variant="h6">Suggestions</Typography>
                            {activity?.suggestions?.map((suggestion, index) => (
                                <Typography key={index} paragraph> {suggestion}</Typography>
                            ))}

                            <Divider sx={{ my: 2 }} />

                            <Typography variant="h6">Safety Guidelines</Typography>
                            {activity?.safety?.map((safety, index) => (
                                <Typography key={index} paragraph> {safety}</Typography>
                            ))}

                        </CardContent>
                    </Card>
                )}
        </Box>
    )
}

export default ActivityDetail





// import { Box, Card, CardContent, Divider, Typography } from '@mui/material';
// import React, { useEffect, useState } from 'react';
// import { useParams } from 'react-router-dom';
// import { getActivityDetail } from '../services/api';

// const ActivityDetail = () => {
//     const { id } = useParams();
//     const [activity, setActivity] = useState(null);
//     const [recommendation, setRecommendation] = useState(null);
//     const [loading, setLoading] = useState(true);
//     const [error, setError] = useState(null);

//     useEffect(() => {
//         const fetchActivityDetail = async () => {
//             try {
//                 setLoading(true);
//                 const response = await getActivityDetail(id);
//                 setActivity(response.data);
//                 setRecommendation(response.data.recommendation || null);
//             } catch (error) {
//                 console.error('Lỗi khi lấy chi tiết hoạt động:', error);
//                 setError('Không thể tải chi tiết hoạt động.');
//             } finally {
//                 setLoading(false);
//             }
//         };

//         fetchActivityDetail();
//     }, [id]);

//     if (loading) {
//         return (
//             <Box sx={{ maxWidth: 800, mx: 'auto', p: 2 }}>
//                 <Typography>Đang tải...</Typography>
//             </Box>
//         );
//     }

//     if (error) {
//         return (
//             <Box sx={{ maxWidth: 800, mx: 'auto', p: 2 }}>
//                 <Typography color="error">{error}</Typography>
//             </Box>
//         );
//     }

//     if (!activity) {
//         return (
//             <Box sx={{ maxWidth: 800, mx: 'auto', p: 2 }}>
//                 <Typography>Không tìm thấy hoạt động.</Typography>
//             </Box>
//         );
//     }

//     return (
//         <Box sx={{ maxWidth: 800, mx: 'auto', p: 2 }}>
//             <Card sx={{ mb: 2 }}>
//                 <CardContent>
//                     <Typography variant="h5" gutterBottom>
//                         Chi tiết hoạt động
//                     </Typography>
//                     <Typography>Loại: {activity.type}</Typography>
//                     <Typography>Thời gian: {activity.duration} phút</Typography>
//                     <Typography>Calo tiêu thụ: {activity.caloriesBurned}</Typography>
//                     <Typography>
//                         Ngày: {new Date(activity.createdAt).toLocaleString('vi-VN')}
//                     </Typography>
//                 </CardContent>
//             </Card>

//             {recommendation && (
//                 <Card>
//                     <CardContent>
//                         <Typography variant="h5" gutterBottom>
//                             Khuyến nghị AI
//                         </Typography>
//                         <Typography variant="h6">Phân tích</Typography>
//                         <Typography paragraph>{recommendation}</Typography>
//                         <Divider sx={{ my: 2 }} />

//                         <Typography variant="h6">Cải thiện</Typography>
//                         {activity?.improvements?.length > 0 ? (
//                             activity.improvements.map((improvement, index) => (
//                                 <Typography key={index} paragraph>
//                                     • {improvement}
//                                 </Typography>
//                             ))
//                         ) : (
//                             <Typography paragraph>Không có đề xuất cải thiện.</Typography>
//                         )}

//                         <Divider sx={{ my: 2 }} />

//                         <Typography variant="h6">Gợi ý</Typography>
//                         {activity?.suggestions?.length > 0 ? (
//                             activity.suggestions.map((suggestion, index) => (
//                                 <Typography key={index} paragraph>
//                                     • {suggestion}
//                                 </Typography>
//                             ))
//                         ) : (
//                             <Typography paragraph>Không có gợi ý.</Typography>
//                         )}

//                         <Divider sx={{ my: 2 }} />

//                         <Typography variant="h6">Hướng dẫn an toàn</Typography>
//                         {activity?.safety?.length > 0 ? (
//                             activity.safety.map((safety, index) => (
//                                 <Typography key={index} paragraph>
//                                     • {safety}
//                                 </Typography>
//                             ))
//                         ) : (
//                             <Typography paragraph>Không có hướng dẫn an toàn.</Typography>
//                         )}
//                     </CardContent>
//                 </Card>
//             )}
//         </Box>
//     );
// };

// export default ActivityDetail;